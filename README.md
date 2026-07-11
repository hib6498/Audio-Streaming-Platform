# Audio Streaming Platform

A SoundCloud-inspired audio streaming platform built to explore production-grade system design patterns including presigned upload flows, asynchronous media processing, and multi-service containerized deployment.

---

## Architecture Overview

```
Client
  |
  |-- POST /api/v1/tracks/upload/presigned-url --> Audio Service (Spring Boot)
  |                                                        |
  |                                                   Garage (S3)
  |                                                        |
  |-- PUT (presigned URL) -------------------------------->|
  |
  |-- POST /api/v1/tracks/upload/complete ---------> Audio Service
                                                           |
                                                    RabbitMQ (track.transcode)
                                                           |
                                               Python Transcoding Worker
                                                           |
                                                    ffprobe + ffmpeg
                                                           |
                                                    Garage (S3)
                                                           |
                                                    RabbitMQ (track.transcode.complete)
                                                           |
                                               Audio Service RabbitListener
                                                           |
                                                      PostgreSQL
```

Schema
![er_diagram](https://github.com/hib6498/Audio-Streaming-Platform/blob/5ba5a80de53177357db8616bc4db50d0b95e1e21/er_diagram.png)
---

## Services

### Auth Service (Spring Boot)

Handles user identity and issues credentials consumed by the audio service.

Responsibilities:
- User registration and login
- Issues JWT tokens for authenticated sessions
- Publishes user info events to RabbitMQ (`user` queue) on profile changes so the audio service stays in sync without direct service-to-service calls

### Audio Service (Spring Boot)

The core API server, authenticated via JWT tokens issued by the auth service.

Exposes:
- `/api/v1/**` — versioned REST API for tracks, playlists, artists, and user profiles

Responsibilities:
- Consumes user info events from RabbitMQ to maintain a local user read model
- Issues presigned PUT URLs for direct client-to-S3 uploads
- Manages track metadata, upload status, and artist relationships
- Publishes transcoding jobs to RabbitMQ after upload confirmation
- Listens for transcoding results and updates track status accordingly
- Serves cover art via a proxy endpoint (Garage is not publicly accessible)
- Runs a scheduled reconciliation job to resolve uploads that never hit the complete endpoint

### Transcoding Worker (Python + Pika)

A standalone worker that consumes from the `track.transcode` queue and processes audio files.

Processing pipeline per track:
1. Download original file from Garage
2. Probe with ffprobe to determine codec and bitrate
3. Decide whether to transcode based on format and bitrate
    - Lossless (FLAC, WAV, AIFF, ALAC) — always transcode to Opus
    - Lossy above 128k — transcode to save bandwidth
    - Lossy at or below 128k — skip transcode, avoid quality degradation
4. Transcode with ffmpeg to Opus/OGG at 128k, or remux with codec copy if skipping
5. Strip all embedded metadata from the output in both cases
6. Upload processed file back to Garage
7. Delete original file
8. Publish result to `track.transcode.complete`

Supports multiple parallel worker processes via `WORKER_COUNT` environment variable. Handles RabbitMQ reconnection, dead lettering for malformed messages, and full cleanup of temporary files on failure.

### Storage (Garage)

Self-hosted S3-compatible object storage. Garage does not support bucket policies, ACLs, or event notifications, so all access control and event handling is managed at the application layer.

Object key structure:
```
tracks/{trackId}/audio.ogg       — transcoded audio
tracks/{trackId}/cover.{ext}     — cover art
```

---

## Key Design Decisions

### Microservices with event-driven user sync

The auth and audio services are independently deployable. Rather than making synchronous calls between them, the auth service publishes user info to a RabbitMQ queue whenever a profile changes. The audio service consumes these events and maintains its own local user read model. This keeps the services decoupled — the audio service never calls the auth service at request time.

### Presigned URL upload flow

Rather than routing file uploads through the application server, the backend issues a short-lived presigned PUT URL. The client uploads directly to Garage and the server is never in the data path. This keeps server memory and bandwidth usage low regardless of file size and allows the upload infrastructure to scale independently.

On upload completion, the client hits `/api/v1/tracks/upload/complete`. A scheduled reconciliation job runs every 60 seconds as a safety net to handle cases where this endpoint is never called — it checks S3 via `headObject` and updates status accordingly.

### Asynchronous transcoding pipeline

Transcoding is offloaded to a separate Python worker via RabbitMQ. The upload endpoint returns immediately after publishing the job, and the track moves through `DRAFT -> PROCESSING -> READY` asynchronously. This prevents long-running ffmpeg processes from blocking HTTP threads and allows the transcoding layer to scale independently of the API.

### Conditional transcoding

The worker probes each file before deciding whether to transcode. Re-encoding an already lossy file at a similar or lower bitrate degrades quality with no benefit. Only files that will meaningfully benefit from transcoding are processed through the ffmpeg encode pipeline — others are remuxed with codec copy to strip metadata while preserving the original audio data exactly.

---

## Data Model

```
tracks
  id, title, genre, description, status, cover_art_key, created_at, updated_at

storage_status
  id, track_id (FK), s3_key, status, expires_at, content_type, file_size, deleted_at

track_artists
  track_id (FK), user_id (FK), role (OWNER | FEATURED), can_edit

users
  id, ...
```

Track status flow:
```
DRAFT -> PROCESSING -> READY
                    -> FAILED
```

Storage status flow:
```
PENDING -> COMPLETED
        -> EXPIRED
        -> FAILED
```

---

## Getting Started

### Prerequisites

- Docker and Docker Compose
- Java 21
- Python 3.12 with uv

### Environment Variables

Each service reads configuration from environment variables. Copy the example files and fill in values:

```bash
cp transcoder/.env.example transcoder/.env
```

Required variables for the transcoder worker:

```
RABBITMQ_URL=amqp://user:password@host:5672/vhost
S3_ENDPOINT=http://127.0.0.1:3900
S3_ACCESS_KEY=
S3_SECRET_KEY=
S3_REGION=garage
S3_BUCKET=audio-streaming
INPUT_QUEUE=track.transcode
OUTPUT_QUEUE=track.transcode.complete
WORKER_COUNT=4
LOG_LEVEL=INFO
```

### Running with Docker Compose

```bash
docker compose up
```

To scale the transcoding worker:

```bash
docker compose up --scale transcoder=4
```

---

## Project Structure

```
audio-streaming/
├── auth-service/                — Spring Boot auth service
│   └── src/main/java/
│       └── com/akash/auth_microservice/
│           ├── user/            — user registration, login, JWT issuance
│           └── configs/         — security and RabbitMQ configuration
|           ...
├── audio-service/               — Spring Boot audio service
│   └── src/main/java/
│       └── org/hib6498/audio_streaming/
│           ├── track/           — track feature (controller, service, repository, entity)
│           ├── playlist/        — playlist feature
│           ├── storage/         — storage feature (S3 service, status entity)
│           ├── user/            — user read model and event consumer
│           └── configs/         — Spring, S3, and RabbitMQ configuration
|           ...
├── transcoder/                  — Python transcoding worker
│   ├── worker.py
│   ├── pyproject.toml
│   ├── uv.lock
│   └── Dockerfile
└── docker-compose.yml
```

---

## Tech Stack

| Layer | Technology                                     |
|---|------------------------------------------------|
| Auth service | Spring Boot, Spring Security, JWT              |
| Audio service | Spring Boot, Spring Cloud AWS, Spring Security |
| Database | PostgreSQL, JPA/Hibernate                      |
| Message queue | RabbitMQ                                       |
| Object storage | Garage (S3-compatible)                         |
| Transcoding | Python, Pika, ffmpeg, ffprobe, boto3           |
| Containerization | Docker                                         |
