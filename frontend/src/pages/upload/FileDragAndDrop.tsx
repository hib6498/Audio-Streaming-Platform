import { useRef, useState, type ChangeEvent, type DragEvent } from "react";
import { cn } from "@sglara/cn";
import ActionButton from "@/components/ActionButton";
import uploadImg from "@/assets/upload.svg";
import { getPresignedPutUrl, uploadTrack } from "@/services/trackService";
import { type TrackPresignedPutResponse } from "@/types/track.types";

interface FileDragAndDropPropTypes {
  file: File | null;
  setFile: React.Dispatch<React.SetStateAction<File | null>>;
  onFileReady: (
    file: File,
    uploadPromise: Promise<void>,
    uploadContextData: TrackPresignedPutResponse
  ) => void;
}

const FileDragAndDrop: React.FC<FileDragAndDropPropTypes> = ({
  file,
  setFile,
  onFileReady,
}) => {
  const [isDragging, setIsDragging] = useState<boolean>(false);
  const ref = useRef<HTMLInputElement>(null);

  const processFile = async (selectedFile: File) => {
    setFile(selectedFile);
    const data = await getPresignedPutUrl({ trackName: selectedFile.name });

    // let it run in background
    const uploadPromise = uploadTrack(
      data.trackId,
      data.uploadId,
      data.s3Url,
      selectedFile,
    );

    onFileReady(selectedFile, uploadPromise, data);
  };

  const handleDrop = (e: DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    setIsDragging(false);
    const dropped = e.dataTransfer.files[0];
    if (dropped) processFile(dropped);
  };

  const handleDragOver = (e: DragEvent<HTMLDivElement>) => e.preventDefault();

  const onFileLoad = (e: ChangeEvent<HTMLInputElement>) => {
    const selected = e.currentTarget.files?.[0];
    if (selected) processFile(selected);
  };

  return (
    <div
      onClick={() => ref.current?.click()}
      onDragOver={handleDragOver}
      onDrop={handleDrop}
      onDragEnter={() => setIsDragging(true)}
      onDragLeave={() => setIsDragging(false)}
      className={cn(
        "border-dashed border-2 border-white/30 hover:border-white rounded-xl my-4 flex flex-col items-center py-10 duration-200 cursor-pointer",
        isDragging && "opacity-50",
      )}
    >
      <span className="invert-100">
        <img src={uploadImg} alt="Upload image" />
      </span>
      <p className="font-bold my-8">
        {file ? file.name : "Drag and drop audio files to get started"}
      </p>
      <ActionButton type="button" className="w-fit px-4 text-md">
        {file ? "Replace File" : "Choose file"}
      </ActionButton>
      <input
        ref={ref}
        type="file"
        name="upload"
        id="file-upload"
        onChange={onFileLoad}
        hidden
      />
    </div>
  );
};

export default FileDragAndDrop;
