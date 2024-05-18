import { Routes, Route } from "react-router-dom";
import { RequireAuth } from "react-auth-kit";
import "./App.css";
import LoginScreen from "./screens/adminScreens/loginPage";
import HomeScreen from "./screens/adminScreens/homeScreen";
import SignupScreen from "./screens/adminScreens/signupScreen";
import AddTaskScreen from "./screens/adminScreens/addNewtask";
import UserSignupScreen from "./screens/userScreens/userSignupScreen";

function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginScreen />} />
      <Route path="/signup" element={<SignupScreen />} />
      <Route path="/users/signup" element={<UserSignupScreen />} />

      <Route
        path="/tasks"
        element={
          <RequireAuth loginPath="/login">
            <HomeScreen />
          </RequireAuth>
        }
      ></Route>
      <Route
        path="/tasks/new"
        element={
          <RequireAuth loginPath="/login">
            <AddTaskScreen />
          </RequireAuth>
        }
      ></Route>
    </Routes>
  );
}

export default App;
