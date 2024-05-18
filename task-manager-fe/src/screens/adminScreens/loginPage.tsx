import { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios, { AxiosError } from "axios";
import { useSignIn } from "react-auth-kit";
import { Form, Input } from "antd";

const CustomerLogin = {
  email: "",
  password: "",
};

function LoginScreen() {
  const navigate = useNavigate();
  const [error, setError] = useState("");
  const signIn = useSignIn();
  const [form] = Form.useForm();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const homePage = () => {
    navigate("/tasks");
  };

  const loginUser = async () => {
    CustomerLogin.email = email;
    CustomerLogin.password = password;

    setError("");

    try {
      const response = await axios.post(
        "http://localhost:2024/api/v1/user-mgmt/login",
        CustomerLogin
      );

      const data = response.data.responseData;

      const accessToken = data.authResponse.accessToken;
      const refreshToken = data.authResponse.refreshToken;
      const role = data.role;

      localStorage.setItem("accessToken", accessToken);
      localStorage.setItem("refreshToken", refreshToken);
      localStorage.setItem("role", role);
      localStorage.setItem("firstName", data.firstName);
      localStorage.setItem("lastName", data.lastName);

      console.log("Access Token:  " + accessToken);

      signIn({
        token: response.data.responseData.accessToken,
        refreshToken: response.data.responseData.refreshToken,
        expiresIn: 30, // minutes
        tokenType: "Bearer",
        authState: { email: email },
      });

      localStorage.setItem("email", email);

      console.log(response.data);
      // Rederict to home page on successful login
      homePage();
    } catch (err) {
      if (err && err instanceof AxiosError) {
        if(err.response?.data.code === '401'){
          setError("Unauthorized user")
        }
        setError(err.response?.data.description);
      } else if (err && err instanceof Error) setError(err.message);

      console.log("Error: ", err);
    }
  };

  return (
    <div className="bg-sky-950 w-[100%]">
      <div className="gap-5 flex max-md:flex-col max-md:items-stretch max-md:gap-0">
        <div className="md:container md:mx-auto px-8 py-20 md:w-8/12 lg:w-5/12">
          <div className="items-center shadow-lg bg-white flex flex-col px-8 rounded-2xl max-md:px-5">
            <div className="justify-center items-stretch mt-7 self-center flex w-[100px] max-w-full gap-0 ">
              <div className="text-violet-700 text-6xl leading-[51px] tracking-wide">
                <span className="font-serif text-black">Simple</span>
                <span className="font-extrabold text-violet-700">Task Manager</span>
              </div>
            </div>
            <div className="text-gray-900 text-lg font-bold leading-9 whitespace-nowrap md:whitespace-normal mt-1">
              Welcome back To Task Manager{" "}
            </div>
            <Form
              form={form}
              name="signup"
              scrollToFirstError
              onFinish={loginUser}
              className="w-full max-w-lg"
            >
              <div className="mt-10">
                <p className="text-left text-red-700">{error}</p>
              </div>

              <div>
                <label
                  className="block uppercase text-left w-full tracking-wide text-gray-700 text-xs font-bold mb-2"
                  htmlFor="grid-email"
                >
                  Email
                </label>
                <Form.Item
                  name="email"
                  rules={[
                    {
                      type: "email",
                      message: "The input is not valid E-mail!",
                    },
                    {
                      required: true,
                      message: "Please input your E-mail!",
                    },
                  ]}
                >
                  <Input
                    className="appearance-none block w-full bg-gray-200 text-gray-700 border border-gray-200 rounded py-3 px-4 mb-1 leading-tight focus:outline-none focus:bg-white"
                    id="grid-email"
                    onChange={(e) => setEmail(e.target.value)}
                  />
                </Form.Item>

                <label
                  className="block uppercase text-left w-full tracking-wide text-gray-700 text-xs font-bold mb-2"
                  htmlFor="grid-password"
                >
                  Password
                </label>

                <Form.Item
                  name="password"
                  rules={[
                    {
                      required: true,
                      message: "Please input your password!",
                    },
                  ]}
                  hasFeedback
                >
                  <Input.Password
                    className="py-3 w-full px-3 "
                    id="grid-password"
                    onChange={(e) => setPassword(e.target.value)}
                  />
                </Form.Item>
              </div>

              <div className="items-stretch self-stretch flex flex-col my-8 max-md:max-w-full">
                <button
                  className="text-white text-sm font-semibold leading-5 whitespace-nowrap justify-center items-center bg-violet-500 px-5 py-3 rounded-lg max-md:max-w-full hover:bg-violet-700 border-none"
                  type="button"
                  onClick={loginUser}
                >
                  Login
                </button>
              </div>
            </Form>
            <div className=" text-sm leading-5  self-center  mt-5 mb-5 md:flex md:flex-col items-stretch">
              <span className=" text-gray-400">Don't have an account ? </span>
              <a
                href="/users/signup"
                className="font-semibold text-violet-700 underline"
              >
                Sign up here{"   "}
              </a>
              {/* <p className="text-gray-700">or</p>
              <a
                href="/reset_password"
                className="font-semibold text-violet-700 underline"
              >
                {" "}
                Forgot password?
              </a> */}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default LoginScreen;
