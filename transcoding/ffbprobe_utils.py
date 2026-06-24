import subprocess
import json

def probe_audio(file_path: str) -> dict:
    result = subprocess.run([
        "ffprobe",
        "-v", "quiet",
        "-of", "json",
        "-show_streams",
        "-select_streams", "a:0",  # assume the uploader is not gonna give a container with multiple streams
        file_path
    ], capture_output=True, text=True)
    
    data = json.loads(result.stdout)
    stream = data["streams"][0]
    
    return {
        "codec": stream.get("codec_name"),
        "bit_rate": int(stream.get("bit_rate", 0)),
        "sample_rate": stream.get("sample_rate"),
        "channels": stream.get("channels"),
    }


LOSSLESS_CODECS = {"flac", "wav", "alac", "aiff", "pcm_s16le", "pcm_s24le", "pcm_s32le"}
TARGET_BITRATE = 128_000

def should_transcode(probe: dict) -> bool:
    codec = probe["codec"]
    bitrate = probe["bit_rate"]

    if codec in LOSSLESS_CODECS:
        return True  # always transcode lossless

    if codec == "opus" and bitrate <= TARGET_BITRATE:
        return False  # already optimal

    if bitrate <= TARGET_BITRATE:
        return False  # lossy at low bitrate, don't degrade further

    return True  # lossy but high bitrate, transcode to save bandwidth