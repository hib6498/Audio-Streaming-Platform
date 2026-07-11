//taken from: https://github.com/nikitadev-yt/react-image-cropper

import { useRef, useState, type ReactEventHandler } from "react";
import ReactCrop, { type Crop, type PixelCrop } from "react-image-crop";
import { convertToPixelCrop } from "react-image-crop";
import { useMutation } from "@tanstack/react-query";
import { AxiosError } from "axios";
import "react-image-crop/dist/ReactCrop.css";
import ActionButton from "@/components/ActionButton";

import setCanvasPreview from "@/lib/setCanvasPreview";
import { compressImage } from "@/utils/imageUtils";
import { blobToFile } from "@/utils/blobUtils";
import { cn } from "@sglara/cn";
import { generateCenteredCrop } from "@/utils/imageUtils";
import { type SelectedFile } from "./SelectedFile.type";
import { type UpdateProfileInfo } from "@/types/profile.types";

import { updateProfile } from "@/services/profileService";

const MIN_DIMENSION = 400;

interface PhotoPreviewProps {
  image: SelectedFile;
  onUpdate: CallableFunction;
}

const PhotoPreview: React.FC<PhotoPreviewProps> = ({ image, onUpdate }) => {
  const [crop, setCrop] = useState<Crop>();
  const [showPreview, setShowPreview] = useState<boolean>(false);
  const imageRef = useRef<HTMLImageElement>(null);
  const canvasPreviewRef = useRef<HTMLCanvasElement>(null);
  const [croppedBlob, setCroppedBlob] = useState<Blob | null>(null);

  const { mutateAsync, isPending, isError } = useMutation<
    void,
    AxiosError,
    UpdateProfileInfo
  >({
    mutationFn: updateProfile,
  });

  //the actual image painted will have smaller dimensions compared to original so
  //this will set the minimum width of 200px x 200px crop relative to painted image in browser
  const minCropWidth = imageRef.current
    ? (MIN_DIMENSION / imageRef.current?.naturalWidth) * imageRef.current?.width
    : 0;

  const handleComplete = (c: PixelCrop) => {
    if (
      !imageRef.current ||
      !canvasPreviewRef.current ||
      !c?.width ||
      !c?.height
    )
      return;
    setCanvasPreview(imageRef.current, canvasPreviewRef.current, c);

    // Convert canvas to blob
    canvasPreviewRef.current.toBlob(
      (blob) => {
        if (blob) {
          setCroppedBlob(blob);
        }
      },
      "image/jpeg",
      0.8, // quality
    );
  };

  const onImageLoad: ReactEventHandler<HTMLImageElement> = (event) => {
    const centeredCrop = generateCenteredCrop(
      event.currentTarget,
      MIN_DIMENSION,
    );
    setCrop(centeredCrop);
    handleComplete(crop as PixelCrop);
  };

  const previewCroppedImage = () => {
    setShowPreview(true);
    setCanvasPreview(
      imageRef.current!,
      canvasPreviewRef.current!,
      convertToPixelCrop(
        crop as Partial<Crop>,
        imageRef.current!.width,
        imageRef.current!.height,
      ),
    );
  };

  const updateProfileAvatar = async () => {
    await (async () => {
      try {
        if (!croppedBlob) throw new Error("Cropped image not loaded");
        //compress if new image was selected
        const compressedFile = await compressImage(
          blobToFile(croppedBlob, image.name),
        );
        //api call
        mutateAsync({ file: blobToFile(compressedFile, image.name) });
        onUpdate();
      } catch (_) {
        //
      }
    })();
  };

  return (
    <div className="">
      <div
        className={cn(
          "container flex items-center justify-center border w-fit h-fit mx-auto my-3 bg-gray-700 relative",
          "min-w-" + MIN_DIMENSION,
        )}
      >
        <ReactCrop
          crop={crop}
          onChange={(_, pr) => setCrop(pr)}
          circularCrop
          keepSelection
          aspect={1}
          minWidth={minCropWidth}
          onComplete={handleComplete}
        >
          <img
            ref={imageRef}
            src={image.src}
            alt="Upload"
            style={{ maxHeight: "50vh" }}
            onLoad={onImageLoad}
          />
        </ReactCrop>

        {/* Canvas Preview (always rendered but shown/hidden using styles) */}
        <div
          className={cn(
            "absolute flex items-center justify-center inset-0 bg-black",
            { hidden: !showPreview },
          )}
        >
          <div className="rounded-full overflow-clip object-contain border">
            <canvas
              ref={canvasPreviewRef}
              style={{
                width: MIN_DIMENSION,
                height: MIN_DIMENSION,
              }}
            />
          </div>
        </div>
      </div>

      {image && crop ? (
        <div className="size-fit mx-auto space-x-2">
          <button
            type="button"
            onClick={() => setShowPreview(false)}
            className={cn(
              "p-2 cursor-pointer",
              !showPreview
                ? "dark:bg-blue-800/30 bg-blue-50 dark:text-white text-blue-600 border-blue-300"
                : "text-gray-500 border-transparent",
            )}
          >
            Crop
          </button>
          <button
            type="button"
            onClick={previewCroppedImage}
            className={cn(
              "p-2 cursor-pointer",
              showPreview
                ? "dark:bg-blue-800/30 bg-blue-50 dark:text-white text-blue-600 border-blue-300"
                : "text-gray-500 border-transparent",
            )}
          >
            Preview
          </button>
        </div>
      ) : null}
      <ActionButton
        type="button"
        onClick={updateProfileAvatar}
        disabled={!crop}
        className="w-fit px-6 disabled:opacity-50 ml-auto"
      >
        {isPending ? "Saving..." : "Save"}
      </ActionButton>
      {isError && <p>Failed</p>}
    </div>
  );
};

export default PhotoPreview;
