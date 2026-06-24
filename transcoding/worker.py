import json
import logging
import os
import tempfile
import time
import pika
import pika.exceptions

from config import WorkerConfig, setup_logging, build_s3_client
from ffmpeg_utils import transcode, strip_metadata
from ffbprobe_utils import probe_audio, should_transcode

logger = logging.getLogger(__name__)


def validate_message(message: dict) -> tuple[str, str]:
    track_id = message.get("trackId")
    s3_key = message.get("s3Key")
    if not track_id or not s3_key:
        raise ValueError(f"Invalid message, missing trackId or s3Key: {message}")
    return str(track_id), str(s3_key)


def process_message(ch, method, properties, body, *, config: WorkerConfig, s3):
    try:
        message = json.loads(body)
    except json.JSONDecodeError as e:
        # malformed message — nack without requeue, will never be valid
        logger.error("Malformed message, discarding | error=%s | body=%s", e, body)
        ch.basic_nack(delivery_tag=method.delivery_tag, requeue=False)
        return

    try:
        track_id, s3_key = validate_message(message)
    except ValueError as e:
        logger.error("Invalid message, discarding | error=%s", e)
        ch.basic_nack(delivery_tag=method.delivery_tag, requeue=False)
        return

    logger.info("Received transcode job | trackId=%s | s3Key=%s", track_id, s3_key)
    start = time.monotonic()

    with tempfile.NamedTemporaryFile(
        suffix=".original", delete=False
    ) as input_file, tempfile.NamedTemporaryFile(
        suffix=".ogg", delete=False
    ) as output_file:

        input_path = input_file.name
        output_path = output_file.name

    try:
        logger.info("Downloading | trackId=%s | key=%s", track_id, s3_key)
        s3.download_file(config.bucket, s3_key, input_path)

        logger.info("Transcoding | trackId=%s", track_id)
        probe = probe_audio(input_path)

        if should_transcode(probe):
            transcode(input_path, output_path)
        else:
            strip_metadata(input_path, output_path, probe["codec"])

        transcoded_key = f"tracks/{track_id}/audio.ogg"
        logger.info("Uploading | trackId=%s | key=%s", track_id, transcoded_key)
        s3.upload_file(
            output_path,
            config.bucket,
            transcoded_key,
            ExtraArgs={"ContentType": "audio/ogg"},
        )

        logger.info("Deleting original | trackId=%s | key=%s", track_id, s3_key)
        s3.delete_object(Bucket=config.bucket, Key=s3_key)

        elapsed = time.monotonic() - start
        logger.info(
            "Transcode complete | trackId=%s | duration=%.2fs", track_id, elapsed
        )

        _publish_result(
            ch,
            config.output_queue,
            {"trackId": track_id, "s3Key": transcoded_key, "status": "SUCCESS"},
        )

        ch.basic_ack(delivery_tag=method.delivery_tag)

    except Exception as e:
        elapsed = time.monotonic() - start
        logger.error(
            "Transcode failed | trackId=%s | duration=%.2fs | error=%s",
            track_id,
            elapsed,
            e,
            exc_info=True,
        )

        _publish_result(
            ch,
            config.output_queue,
            {"trackId": track_id, "status": "FAILED", "error": str(e)},
        )

        # nack without requeue — broken files won't recover on retry
        # they go to the dead letter queue for manual inspection
        ch.basic_nack(delivery_tag=method.delivery_tag, requeue=False)

    finally:
        # always clean up temp files regardless of outcome
        for path in (input_path, output_path):
            try:
                os.unlink(path)
            except OSError:
                pass


def _publish_result(channel, queue: str, payload: dict):
    channel.basic_publish(
        exchange="",
        routing_key=queue,
        body=json.dumps(payload),
        properties=pika.BasicProperties(
            delivery_mode=2,
            content_type="application/json",
            headers={
                "__TypeId__": "org.hib6498.audio_streaming.track.dtos.TrackProcessingCompleteEvent"
            },
        ),
    )


def main():
    setup_logging()

    config = WorkerConfig.from_env()
    s3 = build_s3_client(config)

    logger.info("Worker starting | queue=%s", config.input_queue)

    while True:
        try:
            connection = pika.BlockingConnection(
                pika.URLParameters(config.rabbitmq_url)
            )
            channel = connection.channel()

            channel.queue_declare(
                queue=config.input_queue,
                durable=True,
                arguments={"x-queue-type": "quorum"},
            )
            channel.queue_declare(
                queue=config.output_queue,
                durable=True,
                arguments={"x-queue-type": "quorum"},
            )

            # dead letter queue — nacked messages land here for inspection
            channel.queue_declare(queue=f"{config.input_queue}.dead", durable=True)

            channel.basic_qos(prefetch_count=config.prefetch_count)
            channel.basic_consume(
                queue=config.input_queue,
                on_message_callback=lambda ch, method, props, body: process_message(
                    ch, method, props, body, config=config, s3=s3
                ),
            )

            logger.info("Worker ready | queue=%s", config.input_queue)
            channel.start_consuming()

        except pika.exceptions.ConnectionClosedByBroker as e:
            logger.warning("Connection closed by broker, reconnecting | error=%s", e)

        except pika.exceptions.AMQPChannelError as e:
            logger.error("Channel error, reconnecting | error=%s", e, exc_info=True)

        except pika.exceptions.AMQPConnectionError as e:
            logger.error("Connection lost, reconnecting | error=%s", e)

        except KeyboardInterrupt:
            logger.info("Shutting down")
            break

        time.sleep(config.reconnect_delay_seconds)


if __name__ == "__main__":
    main()
