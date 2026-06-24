from dataclasses import dataclass
import os
import logging
import sys
import boto3
from botocore.config import Config
from dotenv import load_dotenv

load_dotenv()


@dataclass(frozen=True)
class WorkerConfig:
    rabbitmq_url: str
    s3_endpoint: str
    s3_access_key: str
    s3_secret_key: str
    s3_region: str
    bucket: str
    input_queue: str
    output_queue: str
    prefetch_count: int
    max_reconnect_attempts: int
    reconnect_delay_seconds: int

    @staticmethod
    def from_env() -> "WorkerConfig":
        def require(key: str) -> str:
            value = os.getenv(key)
            if not value:
                raise EnvironmentError(f"Required environment variable missing: {key}")
            return value

        return WorkerConfig(
            rabbitmq_url=require("RABBITMQ_URL"),
            s3_endpoint=require("S3_ENDPOINT"),
            s3_access_key=require("S3_ACCESS_KEY"),
            s3_secret_key=require("S3_SECRET_KEY"),
            s3_region=require("S3_REGION"),
            bucket=require("S3_BUCKET"),
            input_queue=os.getenv("INPUT_QUEUE", "track.transcode"),
            output_queue=os.getenv("OUTPUT_QUEUE", "track.transcode.complete"),
            prefetch_count=int(os.getenv("PREFETCH_COUNT", "1")),
            max_reconnect_attempts=int(os.getenv("MAX_RECONNECT_ATTEMPTS", "10")),
            reconnect_delay_seconds=int(os.getenv("RECONNECT_DELAY_SECONDS", "5")),
        )


def setup_logging():
    log_level = os.getenv("LOG_LEVEL", "INFO").upper()
    handler = logging.StreamHandler(sys.stdout)
    handler.setFormatter(
        logging.Formatter(
            fmt="%(asctime)s | %(levelname)-8s | %(name)s | %(message)s",
            datefmt="%Y-%m-%dT%H:%M:%S",
        )
    )
    logging.getLogger().setLevel(log_level)
    logging.getLogger().addHandler(handler)
    logging.getLogger("pika").setLevel(logging.WARNING)
    logging.getLogger("boto3").setLevel(logging.WARNING)
    logging.getLogger("botocore").setLevel(logging.WARNING)


def build_s3_client(config: WorkerConfig):
    return boto3.client(
        "s3",
        endpoint_url=config.s3_endpoint,
        aws_access_key_id=config.s3_access_key,
        aws_secret_access_key=config.s3_secret_key,
        region_name=config.s3_region,
        config=Config(
            signature_version="s3v4",
            retries={"max_attempts": 3, "mode": "adaptive"},  # built-in S3 retry
        ),
    )
