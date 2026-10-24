FROM python:3.12-slim-trixie

COPY --from=ghcr.io/astral-sh/uv:latest /uv /uvx /bin/

WORKDIR /app

# Install FFmpeg and FFprobe binaries
RUN apt-get update \
    && apt-get install -y --no-install-recommends ffmpeg \
    && rm -rf /var/lib/apt/lists/*

COPY . /app

ENV UV_NO_DEV=1

RUN uv sync --locked

CMD ["uv", "run", "worker.py"]