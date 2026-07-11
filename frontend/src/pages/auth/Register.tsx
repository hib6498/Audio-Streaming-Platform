import { useMutation } from "@tanstack/react-query";
import type { AxiosError } from "axios";
import type { RegisterInfo, RegisterResponse } from "@/types/authDTO.types";
import Link from "@/components/Link";
import LabelInput from "@/components/LabelInput";
import { registerUser } from "@/services/authService";
import ActionButton from "@/components/ActionButton";
import { useNavigate } from "react-router-dom";

const Register: React.FC = () => {
  const { mutateAsync, isPending, isError, failureReason } = useMutation<
    RegisterResponse,
    AxiosError,
    RegisterInfo
  >({
    mutationFn: registerUser,
  });

  const navigate = useNavigate();

  const register = async (formData: FormData): Promise<void> => {
    const data: RegisterInfo = {
      username: String(formData.get("username") ?? ""),
      displayName: String(formData.get("display-name") ?? ""),
      email: String(formData.get("email") ?? ""),
      password: String(formData.get("password") ?? ""),
      confirmPassword: String(formData.get("confirm-password") ?? ""),
    };

    try {
      await mutateAsync(data);
      navigate("/login");
    } catch (_) {
      //
    }
  };

  return (
    <div className="flex flex-col w-full min-h-full items-center justify-center content-center">
      <h1 className="text-5xl mx-auto text-center font-bold">Register</h1>
      <form
        action={register}
        className="flex flex-col my-8 max-w-84 mx-auto w-full gap-3"
      >
        <LabelInput name="username" label="Username"></LabelInput>
        <LabelInput name="display-name" label="Display Name"></LabelInput>
        <LabelInput name="email" label="Email"></LabelInput>
        <LabelInput name="password" label="Password"></LabelInput>
        <LabelInput
          name="confirm-password"
          label="Confirm Password"
        ></LabelInput>
        <p className="my-0.5"></p>
        <ActionButton disabled={isPending}>
          {isPending ? "Creating Account..." : "Create"}
        </ActionButton>
      </form>
      <p className="text-center">
        Already have an account? <Link url="/login">Login</Link>
      </p>
      {isError ? <p>{failureReason?.message}</p> : <></>}
    </div>
  );
};

export default Register;
