import { useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import SoundcloudLogo from "@/components/SoundcloudLogo";
import CloseButton from "@/components/CloseButton";
import FileDragAndDrop from "./FileDragAndDrop";
import LabelInput from "@/components/LabelInput";
import ActionButton from "@/components/ActionButton";
import { updateTrackMetadata } from "@/services/trackService";
import { type TrackPresignedPutResponse } from "@/types/track.types";

type UploadStatus = "uploading" | "done" | "error";

interface PartialTrackUploadContext {
  trackId: number;
  uploadId: number;
}

const Upload = () => {
  const [file, setFile] = useState<File | null>(null);
  const [artwork, setArtwork] = useState<File | null>(null);
  const [uploadStatus, setUploadStatus] = useState<UploadStatus | null>(null);
  const [uploadContext, setUploadContext] =
    useState<PartialTrackUploadContext | null>(null);
  const navigate = useNavigate();
  const artworkInputRef = useRef<HTMLInputElement>(null);

  const handleFileReady = (
    selectedFile: File,
    uploadPromise: Promise<void>,
    uploadContextData: TrackPresignedPutResponse,
  ) => {
    setFile(selectedFile);
    setUploadStatus("uploading");

    uploadPromise
      .then(() => {
        setUploadStatus("done");
        setUploadContext({
          trackId: uploadContextData.trackId,
          uploadId: uploadContextData.uploadId,
        });
      })
      .catch(() => setUploadStatus("error"));
  };

  const onSubmit = async (formData: FormData): Promise<void> => {
    const trackName = String(formData.get("track_name") ?? "");
    const ids = String(formData.get("artist_ids") ?? "");
    if (!file || !trackName) return;

    const featureIds = ids
      .split(",")
      .map((id) => id.trim())
      .filter((id) => id !== "")
      .map(Number)
      .filter((id) => !isNaN(id));

    if (!uploadContext) {
      console.log("Track hasn't been uploaded yet.");
      return;
    }
    updateTrackMetadata(
      {
        trackName,
        featureIds: new Set(featureIds),
        coverArt: artwork ?? undefined,
      },
      uploadContext?.trackId,
    );
  };

  return (
    <div>
      <section className="px-4 py-1.5 bg-black">
        <div className="flex items-center">
          <SoundcloudLogo className="size-16" />
          <h1 className="font-bold text-xl pt-2 px-2">
            {file ? "Track info" : "Upload"}
          </h1>
          <CloseButton
            onClick={() => navigate(-1)}
            className="size-12 ml-auto hover:bg-gray-900 rounded-full p-2 cursor-pointer duration-200 border border-gray-500"
          />
        </div>
      </section>

      {!file ? (
        <section className="my-2 px-5 max-w-5xl mx-auto">
          <h1 className="text-3xl font-bold tracking-tight py-3">
            Upload your audio files
          </h1>
          <p className="pt-1">
            For best quality, use WAV, FLAC, AIFF, or ALAC. The maximum file
            size is 500MB uncompressed.
          </p>
          <FileDragAndDrop
            file={file}
            setFile={setFile}
            onFileReady={handleFileReady}
          />
        </section>
      ) : (
        <form
          action={onSubmit}
          className="my-8 px-5 max-w-5xl mx-auto flex flex-col gap-6"
        >
          {/* Upload progress placeholder */}
          <div className="flex items-center gap-3 bg-white/5 border border-white/10 rounded-lg px-4 py-3">
            <div className="size-10 bg-white/10 rounded flex items-center justify-center shrink-0">
              🎵
            </div>
            <div className="flex flex-col flex-1 min-w-0">
              <span className="text-sm font-medium truncate">{file.name}</span>
              {uploadStatus === "uploading" && (
                <div className="flex items-center gap-2 mt-1">
                  <div className="flex-1 h-1 bg-white/10 rounded-full overflow-hidden">
                    <div className="h-full bg-orange-500 rounded-full animate-[progress_2s_ease-in-out_infinite]" />
                  </div>
                  <span className="text-xs text-white/50 shrink-0">
                    Uploading...
                  </span>
                </div>
              )}
              {uploadStatus === "done" && (
                <span className="text-xs text-green-400 mt-1">
                  ✓ Upload complete
                </span>
              )}
              {uploadStatus === "error" && (
                <span className="text-xs text-red-400 mt-1">
                  ✗ Upload failed
                </span>
              )}
            </div>
          </div>

          {/* Metadata + artwork */}
          <div className="flex gap-8 lg:gap-26">
            <div
              onClick={() => artworkInputRef.current?.click()}
              className="w-125 aspect-square cursor-pointer shrink-0"
            >
              {artwork ? (
                <div className="relative group">
                  <img src={URL.createObjectURL(artwork)} alt="Artwork" />
                  <div className="hidden group-hover:flex absolute inset-0 items-end justify-center">
                    <div className="absolute opacity-40 z-1 bg-red-50 size-full" />
                    <button
                      type="button"
                      className="bg-black z-2 h-fit mb-6 opacity-100 rounded-sm p-2 font-semibold hover:text-gray-500 cursor-pointer text-sm"
                    >
                      Update
                    </button>
                  </div>
                </div>
              ) : (
                <p className="w-full h-full border-dashed border hover:border-white duration-200 flex flex-col items-center justify-center text-white/50">
                  <span className="block text-center w-full text-5xl font-extralight">
                    +
                  </span>
                  Add artwork
                </p>
              )}
              <input
                onChange={(e) => setArtwork(e.currentTarget.files?.[0] ?? null)}
                ref={artworkInputRef}
                className="hidden"
                type="file"
                name="artwork"
              />
            </div>

            <div className="w-full flex flex-col gap-3">
              <LabelInput
                label="Track name"
                name="track_name"
                inputProps={{
                  defaultValue: file.name.slice(0, file.name.lastIndexOf(".")),
                }}
              />
              <LabelInput
                name="artist_ids"
                label="Artist ids (separate by commas)"
              />
              <ActionButton
                className="w-fit px-8"
                disabled={uploadStatus === "uploading"}
              >
                {uploadStatus === "uploading" ? "Uploading..." : "Submit"}
              </ActionButton>
            </div>
          </div>
        </form>
      )}
    </div>
  );
};

export default Upload;
