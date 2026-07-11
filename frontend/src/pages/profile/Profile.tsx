import { useRef, useState } from "react";
import Avatar from "@/components/Avatar";
import Layover from "@/components/Layover";
import PhotoPreview from "../artist/PhotoPreview";
import useAuth from "@/hooks/useAuth";
import { isValidImageSize } from "@/utils/imageUtils";
import { type SelectedFile } from "../artist/SelectedFile.type";

const MIN_DIMENSION = 200;

const Profile: React.FC = () => {
  const { user } = useAuth();
  const [showLayover, setShowLayover] = useState(false);
  const [imageSrc, setImageSrc] = useState<SelectedFile | null>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  const selectImage: React.ChangeEventHandler<
    HTMLInputElement,
    HTMLInputElement
  > = (event) => {
    const file = event.target.files?.[0];
    if (!file) return;

    const reader = new FileReader();
    reader.addEventListener("load", () => {
      const imageElement = new Image(); //creating an image object to check its height and width
      const imageUrl = reader.result?.toString() || "";
      imageElement.src = imageUrl;

      imageElement.addEventListener("load", (e: Event) => {
        const { naturalWidth, naturalHeight } =
          e.currentTarget as HTMLImageElement;
        if (isValidImageSize(naturalWidth, naturalHeight, MIN_DIMENSION)) {
          console.log("insufficient image size");
          setImageSrc(null);
        } else {
          setImageSrc({ src: imageUrl, name: file.name });
        }
      });
    });
    reader.readAsDataURL(file);
  };

  if (!user) return <div>loading...</div>;

  return (
    <>
      {showLayover && imageSrc && (
        <Layover
          closeCallback={() => {
            setShowLayover(false);
            setImageSrc(null);
          }}
        >
          <div className="py-8 px-12 rounded-xl bg-black z-2 mx-auto">
            <PhotoPreview
              image={imageSrc}
              onUpdate={() => setShowLayover(false)}
            />
          </div>
        </Layover>
      )}

      <section className="max-w-6xl mx-auto px-4">
        <div className="relative flex items-center gap-12 px-5 py-8 overflow-hidden">
          <div
            className="absolute inset-0 -z-2"
            style={{
              backgroundImage: `url(${user.profilePic})`,
              backgroundSize: "cover",
              backgroundPosition: "center",
            }}
          >
            <div className="w-full h-full backdrop-blur-xl scale-110"></div>
          </div>
          <div className="absolute inset-0 bg-black/70 -z-1"></div>
          <div className="relative z-10 flex items-center gap-12">
            <div className="relative w-fit group">
              <Avatar user={user} size="large" />

              <div className="hidden group-hover:flex absolute inset-0 overflow-clip justify-center">
                <div className="absolute opacity-40 z-1 bg-red-50 size-full rounded-full"></div>
                <input
                  type="file"
                  accept=".jpg, .jpeg, .png"
                  name="profilePi"
                  className="size-px col-fit"
                  onChange={selectImage}
                  ref={inputRef}
                />
                <button
                  onClick={() => {
                    inputRef.current?.click();
                    setShowLayover(true);
                  }}
                  className="bg-black z-2 h-fit mt-32 opacity-100 rounded-sm p-2 font-semibold hover:text-gray-500 cursor-pointer text-sm"
                >
                  Update
                </button>
              </div>
            </div>
          </div>

          <div>
            <h1 className="bg-gray-950 text-2xl font-bold w-fit px-3">
              {user.username}
            </h1>
            <h1 className="bg-gray-950 font-semibold opacity-85 px-3 w-fit">
              {user.displayName}
            </h1>
          </div>
        </div>
      </section>
    </>
  );
};

export default Profile;
