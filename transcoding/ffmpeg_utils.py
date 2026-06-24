import ffmpeg


def strip_metadata(input_path: str, output_path: str, codec: str):
    (
        ffmpeg.input(input_path)
        .output(
            output_path,
            acodec="copy",  # no re-encoding, just remux
            map_metadata=-1,  # strip all metadata
            vn=None,  # strip embedded cover art (video stream)
        )
        .overwrite_output()
        .run(quiet=True)
    )


def transcode(input_path: str, output_path: str):
    try:
        (
            ffmpeg.input(input_path)
            .output(
                output_path,
                map="0:a",
                acodec="libopus",
                **{"b:a": "128k"},
                map_metadata=-1,
            )
            .overwrite_output()
            .run(quiet=True)
        )
    except ffmpeg.Error as e:
        # ffmpeg.Error swallows the stderr — extract it for logging
        raise RuntimeError(f"ffmpeg failed: {e.stderr.decode()}") from e
