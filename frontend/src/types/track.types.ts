// interface TrackUploadDTO {
//   trackName: string;
//   trackFile: File;
//   featureIds?: string;
//   coverArt?: File;
// }

interface TrackUserInfo {
  userId: number;
  displayName: string;
}

interface TrackInfo {
  trackId: number;
  title: string;
  createdAt: EpochTimeStamp;
  owns: TrackUserInfo;
  features: TrackUserInfo[];
  artworkUrl: string;
}

interface TracksForArtist {
  trackId: number;
  trackInfo: TrackInfo;
}

interface TrackPresignedPutRequest {
  trackName: string;
}

interface TrackPresignedPutResponse {
  trackId: number;
  uploadId: number;
  s3Url: string;
}

interface TrackUploadCompleteEvent {
  trackId: number;
  uploadId: number;
}

interface UpdateTrackMetadataRequest {
  trackName: string;
  coverArt?: File;
  featureIds: Set<number>;
}

export type {
  TracksForArtist,
  TrackInfo,
  TrackUserInfo,
  TrackPresignedPutRequest,
  TrackPresignedPutResponse,
  TrackUploadCompleteEvent,
  UpdateTrackMetadataRequest
};
