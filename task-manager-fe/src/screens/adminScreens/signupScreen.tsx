import { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios, { AxiosError } from "axios";
import { Checkbox, Form, Input, Row, Col, Button, Typography } from "antd";

const { Title, Text } = Typography;

const SignupScreen = () => {
  const navigate = useNavigate();
  const [error, setError] = useState("");
  const [form] = Form.useForm();

  const saveUser = async (values: any) => {
    if (!values.agreement) {
      setError("Agreement box has not been checked");
      return;
    }

    setError("");

    try {
      const response = await axios.post("http://localhost:2024/api/v1/user-mgmt/admin/signup", values);
      console.log(response.data);
      navigate("/login");
    } catch (err) {
      if (err && err instanceof AxiosError) setError(err.response?.data.description);
      else if (err && err instanceof Error) setError(err.message);
      console.log("Error: ", err);
    }
  };

  return (
    <div className="container mx-auto px-4 py-20 bg-sky-950">
      <div className="md:w-8/12 lg:w-5/12 mx-auto bg-white p-6 rounded-2xl shadow-lg">
        <div className="text-center mb-6">
          <Title level={2} className="text-black">
            Simple <span className="font-extrabold text-violet-700">Task Manager</span>
          </Title>
          <Text className="text-lg font-bold">Create a new account</Text>
        </div>
        {error && <Text type="danger">{error}</Text>}
        <Form
          form={form}
          name="signup"
          layout="vertical"
          onFinish={saveUser}
          autoComplete="off"
        >
          <Row gutter={16}>
            <Col xs={24} md={12}>
              <Form.Item
                name="firstName"
                label="First Name"
                rules={[{ required: true, message: "Please input your First name!" }]}
              >
                <Input placeholder="First Name" />
              </Form.Item>
            </Col>
            <Col xs={24} md={12}>
              <Form.Item
                name="lastName"
                label="Last Name"
                rules={[{ required: true, message: "Please input your Last name!" }]}
              >
                <Input placeholder="Last Name" />
              </Form.Item>
            </Col>
          </Row>
          <Form.Item
            name="email"
            label="Email"
            rules={[
              { type: "email", message: "The input is not valid E-mail!" },
              { required: true, message: "Please input your E-mail!" },
            ]}
          >
            <Input placeholder="Email" />
          </Form.Item>
          <Row gutter={16}>
            <Col xs={24} md={12}>
              <Form.Item
                name="password"
                label="Password"
                rules={[
                  { required: true, message: "Please input your password!" },
                  { min: 4, message: "Password is too short!" },
                ]}
                hasFeedback
              >
                <Input.Password placeholder="Password" />
              </Form.Item>
            </Col>
            <Col xs={24} md={12}>
              <Form.Item
                name="confirmPassword"
                label="Confirm Password"
                dependencies={["password"]}
                hasFeedback
                rules={[
                  { required: true, message: "Please confirm your password!" },
                  ({ getFieldValue }) => ({
                    validator(_, value) {
                      if (!value || getFieldValue("password") === value) {
                        return Promise.resolve();
                      }
                      return Promise.reject(new Error("The new password that you entered do not match!"));
                    },
                  }),
                ]}
              >
                <Input.Password placeholder="Confirm Password" />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={16}>
            <Col xs={24} md={12}>
              <Form.Item
                name="address"
                label="Address"
              >
                <Input placeholder="Address" />
              </Form.Item>
            </Col>
            <Col xs={24} md={12}>
              <Form.Item
                name="phoneNumber"
                label="Phone Number"
              >
                <Input placeholder="080********" />
              </Form.Item>
            </Col>
          </Row>
          <Form.Item
            name="gender"
            label="Gender"
          >
            <Input.Group compact>
              <Form.Item
                name="gender"
                noStyle
              >
                <select className="block w-full bg-gray-200 border border-gray-200 text-gray-700 py-3 px-4 pr-8 rounded leading-tight focus:outline-none focus:bg-white focus:border-gray-500">
                  <option value="">Select gender</option>
                  <option value="MALE">Male</option>
                  <option value="FEMALE">Female</option>
                </select>
              </Form.Item>
            </Input.Group>
          </Form.Item>
          <Form.Item
            name="agreement"
            valuePropName="checked"
            rules={[
              {
                validator: (_, value) =>
                  value ? Promise.resolve() : Promise.reject(new Error("Should accept agreement")),
                required: true,
              },
            ]}
          >
            <Checkbox>
              I have read the <a href="https://primerspace.com/agreement" className="text-blue">agreement</a>
            </Checkbox>
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" className="w-full">
              Admin Sign Up
            </Button>
          </Form.Item>
        </Form>
        <div className="text-center mt-4">
          <Text className="text-gray-400">Already have an account?</Text>
          <a href="/login" className="font-semibold text-violet-700 underline ml-2">Sign in here</a>
        </div>
      </div>
    </div>
  );
};

export default SignupScreen;
