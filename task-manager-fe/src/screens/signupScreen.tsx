import { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios, { AxiosError } from "axios";
import { Checkbox, Form, Input } from "antd";

const Customer = {
  firstName: "",
  lastName: "",
  email: "",
  password: "",
  confirmPassword: "",
  phoneNumber: "",
  address: "",
  gender: "",
};

function SignupScreen() {
  const navigate = useNavigate();
  const [error, setError] = useState("");
  const [form] = Form.useForm();

  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [address, setAddress] = useState("");
  const [gender, setGender] = useState("");
  const [agreement, setAggreement] = useState(false);

  const loginPage = () => {
    navigate("/login");
  };

  const saveUser = async () => {
    if (!agreement) {
      setError("Agreement box has not been checked");
      return;
    }
    Customer.firstName = firstName;
    Customer.lastName = lastName;
    Customer.email = email;
    Customer.password = password;
    Customer.confirmPassword = confirmPassword;
    Customer.address = address;
    Customer.phoneNumber = phoneNumber;
    Customer.gender = gender;

    setError("");

    try {
      await axios
        .post("http://localhost:2024/api/v1/user-mgmt/admin/signup", Customer)
        .then((response) => {
          console.log(response.data);

          loginPage();
        });
    } catch (err) {
      if (err && err instanceof AxiosError)
        setError(err.response?.data.responseMessage);
      else if (err && err instanceof Error) setError(err.message);

      console.log("Error: ", err);
    }
  };

  const handleInputChange = (e: any) => {
    const { name, value, checked } = e.target;

    if (name === "firstName") {
      setFirstName(value);
      return;
    } else if (name === "lastName") {
      setLastName(value);
      return;
    } else if (name === "email") {
      setEmail(value);
      return;
    } else if (name === "password") {
      setPassword(value);
      return;
    } else if (name === "confirmPassword") {
      setConfirmPassword(value);
      return;
    } else if (name === "address") {
      setAddress(value);
      return;
    } else if (name === "phoneNumber") {
      setPhoneNumber(value);
      return;
    } else if (name === "gender") {
      setGender(value);
      return;
    } else if (name === "checkBox") {
      setAggreement(checked);
      return;
    }
  };

  return (
    <div className="bg-sky-950 container mx-auto">
      {/* <div className="gap-5 flex max-md:flex-col max-md:items-stretch max-md:gap-0"> */}
      <div className="md:container md:mx-auto px-8 py-20 md:w-8/12 lg:w-5/12">
        <div className="items-center shadow-lg bg-white flex flex-col px-8 rounded-2xl max-md:px-5">
          <div className="justify-center items-stretch mt-7 self-center flex w-[100px] max-w-full gap-0 ">
            <div className="text-violet-700 text-6xl leading-[51px] tracking-wide">
              <span className="font-serif text-black">Simple</span>
              <span className="font-extrabold text-violet-700">
                Task Manager
              </span>
            </div>
          </div>
          <div className="text-gray-900 text-lg font-bold leading-9 whitespace-nowrap md:whitespace-normal mt-1">
            Create a new account{" "}
          </div>
          <Form
            form={form}
            name="signup"
            scrollToFirstError
            onFinish={saveUser}
            className="md:w-full md:flex md:flex-col md:items-stretch"
            autoComplete="off"
          >
            <span className="text-red">{error}</span>
            <div className={`flex flex-row ${error ? "mt-2" : "mt-7"} -mx-3`}>
              <div className="w-full md:w-1/2 px-3 md:mb-0 relative">
                <label
                  className="block uppercase text-left  tracking-wide text-gray-700 text-xs font-bold mb-2 mt-1"
                  htmlFor="grid-first-name"
                >
                  First Name
                </label>
                <Form.Item
                  name="firstName"
                  rules={[
                    {
                      required: true,
                      message: "Please input your First name!",
                    },
                  ]}
                >
                  <Input
                    className="appearance-none block w-full bg-gray-200 text-gray-700 border border-gray-200 rounded py-3 px-4 mb-1 leading-tight focus:outline-none focus:bg-white"
                    id="grid-first-name"
                    name="firstName"
                    onChange={handleInputChange}
                  />
                </Form.Item>
              </div>
              <div className="w-full md:w-1/2 px-3">
                <label
                  className="block uppercase text-left w-full tracking-wide text-gray-700 text-xs font-bold mb-2 mt-1"
                  htmlFor="grid-last-name"
                >
                  Last Name
                </label>
                <Form.Item
                  name="lastName"
                  rules={[
                    {
                      required: true,
                      message: "Please input your Last name!",
                    },
                  ]}
                >
                  <Input
                    // type="text"
                    className="appearance-none block w-full bg-gray-200 text-gray-700 border border-gray-200 rounded py-3 px-4 mb-1 leading-tight focus:outline-none focus:bg-white"
                    id="grid-last-name"
                    name="lastName"
                    onChange={handleInputChange}
                  />
                </Form.Item>
              </div>
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
                // className="w-full px-3 mb-1"
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
                  name="email"
                  onChange={handleInputChange}
                />
              </Form.Item>
              <div className={`flex flex-row ${error ? "mt-2" : "mt-7"} -mx-3`}>
                <div className="w-full px-3">
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
                      () => ({
                        validator(_, value) {
                          if (value.toString().length >= 4) {
                            return Promise.resolve();
                          }
                          return Promise.reject(
                            new Error("Password is too short!")
                          );
                        },
                      }),
                    ]}
                    hasFeedback
                  >
                    <Input.Password
                      className="py-3 w-full px-3 "
                      name="password"
                      onChange={handleInputChange}
                    />
                  </Form.Item>
                </div>
                <div className="w-full px-3">
                  <label
                    className="block uppercase text-left w-full tracking-wide text-gray-700 text-xs font-bold mb-2"
                    htmlFor="grid-confirm-password"
                  >
                    Confirm Password
                  </label>

                  <Form.Item
                    name="confirmPassword"
                    dependencies={["password"]}
                    hasFeedback
                    rules={[
                      {
                        required: true,
                        message: "Please confirm your password!",
                      },
                      ({ getFieldValue }) => ({
                        validator(_, value) {
                          if (!value || getFieldValue("password") === value) {
                            return Promise.resolve();
                          }
                          return Promise.reject(
                            new Error(
                              "The new password that you entered do not match!"
                            )
                          );
                        },
                      }),
                    ]}
                  >
                    <Input.Password
                      className="py-3 w-full px-3 border-solid"
                      name="confirmPassword"
                      onChange={handleInputChange}
                    />
                  </Form.Item>
                </div>
              </div>
            </div>
            <div className="flex flex-row -mx-3 mb-6 ">
              <div className="w-full md:w-1/2 px-3 mb-6 md:mb-0 relative">
                <label
                  className="block uppercase tracking-wide text-gray-700 text-xs font-bold mb-2"
                  htmlFor="address"
                >
                  Address
                </label>
                <input
                  className="appearance-none block  bg-gray-200 text-gray-700 border border-gray-0 rounded py-3 px-2 mb-3 leading-tight focus:outline-none focus:bg-white focus:border-gray-500"
                  id="addresss"
                  type="text"
                  name="address"
                  placeholder="Address"
                  onChange={handleInputChange}
                  autoComplete="off"
                />
              </div>

              <div className="w-full md:w-1/2 px-3 mb-6 md:mb-0 relative">
                <label
                  className="block uppercase tracking-wide text-gray-700 text-xs font-bold mb-2"
                  htmlFor="grid-phone"
                >
                  Phone Number
                </label>
                <input
                  className="appearance-none block bg-gray-200 text-gray-700 border border-gray-200 rounded py-3 px-2 leading-tight focus:outline-none focus:bg-white focus:border-gray-500"
                  id="grid-phone"
                  type="tel"
                  name="phoneNumber"
                  placeholder="080********"
                  onChange={handleInputChange}
                />
              </div>
            </div>
            <div className="flex  -mx-3 mb-6 justify-center">
              <div className="w-full md:w-1/2 px-3 mb-6 md:mb-0 relative">
                <label
                  className="block uppercase tracking-wide text-gray-700 text-xs font-bold mb-2"
                  htmlFor="grid-gender"
                >
                  Gender
                </label>
                <div className="relative">
                  <select
                    className="block appearance-none w-full bg-gray-200 border border-gray-200 text-gray-700 py-3 px-4 pr-8 rounded leading-tight focus:outline-none focus:bg-white focus:border-gray-500"
                    id="grid-gender"
                    name="gender"
                    onChange={handleInputChange}
                  >
                    <option value="">Select gender</option>
                    <option value="MALE">Male</option>
                    <option value="FEMALE">Female</option>
                  </select>
                  <div className="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-gray-700">
                    <svg
                      className="fill-current h-4 w-4"
                      xmlns="http://www.w3.org/2000/svg"
                      viewBox="0 0 20 20"
                    >
                      <path d="M9.293 12.95l.707.707L15.657 8l-1.414-1.414L10 10.828 5.757 6.586 4.343 8z" />
                    </svg>
                  </div>
                </div>
              </div>
            </div>
            <div className="w-full max-w-lg">
              <Form.Item
                name="aggreement"
                valuePropName="checked"
                className="w-full max-w-lg"
                rules={[
                  {
                    validator: (_, value) =>
                      value
                        ? Promise.resolve()
                        : Promise.reject(new Error("Should accept agreement")),
                    required: true,
                  },
                ]}
              >
                <Checkbox onChange={handleInputChange} name="checkBox">
                  I have read the{" "}
                  <a
                    href="https://primerspace.com/agreement"
                    className="text-blue"
                  >
                    agreement
                  </a>
                </Checkbox>
              </Form.Item>
            </div>
            <div className="items-stretch self-stretch flex flex-col my-4 max-md:max-w-full">
              <button
                className="text-white text-sm font-semibold leading-5 whitespace-nowrap justify-center items-center bg-violet-500 px-5 py-3 rounded-lg max-md:max-w-full hover:bg-voilet-700"
                type="button"
                onClick={saveUser}
              >
                Sign Up
              </button>
            </div>
          </Form>
          <div className="text-sm leading-5  self-center whitespace-normal mt-5 mb-5 md:flex md:flex-col items-stretch">
            <span className=" text-gray-400">Already have an account ? </span>
            <a
              href="/login"
              className="font-semibold text-voilet-700 underline"
            >
              Sign in here
            </a>
          </div>
        </div>
      </div>
      {/* </div> */}
    </div>
  );
}

export default SignupScreen;
