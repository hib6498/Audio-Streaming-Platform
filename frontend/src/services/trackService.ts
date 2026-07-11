import { instance as audioAxiosInstance } from "@/config/trackAxiosInstance";
import type {
  TrackInfo,
  TrackPresignedPutRequest,
  TrackPresignedPutResponse,
  TracksForArtist,
  TrackUploadCompleteEvent,
  UpdateTrackMetadataRequest,
  // TrackUploadDTO,
} from "@/types/track.types";
import axios from "axios";

// const uploadTrack = async (track: TrackUploadDTO) => {
//   const { data } = await audioAxiosInstance.post("/track/upload", track, {
//     headers: { "Content-Type": "multipart/form-data" },
//   });
//   console.dir(data);
// };

const getTracksForArtists = async (
  userId: number,
): Promise<TracksForArtist[]> => {
  const { data } = await audioAxiosInstance.get<TracksForArtist[]>(
    `/user/${userId}/discography`,
  );
  return data;
};

const getAllTracks = async (): Promise<TracksForArtist[]> => {
  const { data } =
    await audioAxiosInstance.get<TracksForArtist[]>("/track/all");
  return data;
};

const getTrack = async (trackId: number): Promise<TrackInfo> => {
  const { data } = await audioAxiosInstance.get<TrackInfo>(`/track/${trackId}`);
  return data;
};

const getTrackStreamUrl = async (trackId: number): Promise<string> => {
  const { data } = await audioAxiosInstance.get<string>(
    `track/${trackId}/stream-url`,
  );
  return data;
};

const getPresignedPutUrl = async (
  trackName: TrackPresignedPutRequest,
): Promise<TrackPresignedPutResponse> => {
  const { data } = await audioAxiosInstance.post<TrackPresignedPutResponse>(
    "/track/upload/presigned-url",
    trackName,
  );
  return data;
};

const markTrackCompleteEvent = async (
  body: TrackUploadCompleteEvent,
): Promise<void> => {
  return (await audioAxiosInstance.post<void>("/track/upload/complete", body))
    .data;
};

const uploadTrack = async (
  trackId: number,
  uploadId: number,
  s3Url: string,
  file: File,
): Promise<void> => {
  return await axios
    .put<void>(s3Url, file, {
      headers: { "Content-Type": file.type },
      onUploadProgress: () => {},
    })
    .then(() => markTrackCompleteEvent({ trackId, uploadId }));
};

const updateTrackMetadata = async (
  request: UpdateTrackMetadataRequest,
  id: number,
): Promise<TrackInfo> => {
  const formData = new FormData();
  formData.append("trackName", request.trackName);
  if (request.coverArt) formData.append("coverArt", request.coverArt);
  request.featureIds.forEach((id) => formData.append("featureIds", String(id)));

  const { data } = await audioAxiosInstance.put<TrackInfo>(
    `/track/${id}`,
    formData,
    { headers: { "Content-Type": "multipart/form-data" } },
  );
  return data;
};

export {
  uploadTrack,
  updateTrackMetadata,
  getTracksForArtists,
  getAllTracks,
  getPresignedPutUrl,
  getTrack,
  getTrackStreamUrl,
};
