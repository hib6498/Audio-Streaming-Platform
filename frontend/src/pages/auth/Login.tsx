import { useMutation } from "@tanstack/react-query";
import type { AxiosError } from "axios";
import useAuth from "@/hooks/useAuth";
import { loginUser } from "@/services/authService";
import type { LoginResponse, LoginInfo } from "@/types/authDTO.types";
import LabelInput from "@/components/LabelInput";
import Link from "@/components/Link";
import SoundcloudLogo from "@/components/SoundcloudLogo";
import ActionButton from "@/components/ActionButton";

const Login: React.FC = () => {
  const { login } = useAuth();

  const { mutateAsync, isPending, isError } = useMutation<
    LoginResponse,
    AxiosError,
    LoginInfo
  >({
    mutationFn: loginUser,
  });

  const loginRequest = async (formData: FormData): Promise<void> => {
    const data: LoginInfo = {
      username: String(formData.get("username") ?? ""),
      password: String(formData.get("password") ?? ""),
    };

    try {
      const response = await mutateAsync(data);
      login(response.accessToken, "/");
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
    } catch (_error) {
      //
    }
  };

  return (
    <div className="flex flex-col w-full min-h-full items-center mt-15 content-center">
      <SoundcloudLogo className={"size-48"} />
      <h1 className="text-5xl mx-auto text-center font-bold">Login</h1>
      <form
        action={loginRequest}
        className="flex flex-col my-8 max-w-84 mx-auto w-full gap-4"
      >
        <LabelInput name="username" label="Username"></LabelInput>
        <LabelInput name="password" label="Password"></LabelInput>
        <p className="my-0.5"></p>
        <ActionButton type="submit" disabled={isPending}>
          {isPending ? "Logging in..." : "Continue"}
        </ActionButton>
      </form>
      <p className="text-center">
        Don't have an account? <Link url="/register">Register</Link>
      </p>
      {isError ? <p className="text-red">Invalid Credentials</p> : <></>}
    </div>
  );
};

export default Login;
