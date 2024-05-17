import { Routes, Route } from "react-router-dom";
import { RequireAuth } from "react-auth-kit";
import './App.css'
import LoginScreen from "./screens/loginPage";
import HomeScreen from "./screens/homeScreen";
import SignupScreen from "./screens/signupScreen";
import AddTaskScreen from "./screens/addNewtask";

function App() {

  return (
    <Routes>
      <Route path="/login" element={<LoginScreen />} />
      <Route path="/signup" element={<SignupScreen />} />

      <Route
          path="/home"
          element={
            <RequireAuth loginPath="/login">
              <HomeScreen />
            </RequireAuth>
          }
        ></Route>
        <Route
          path="/new-task"
          element={
            <RequireAuth loginPath="/login">
              <AddTaskScreen />
            </RequireAuth>
          }
        ></Route>
    </Routes>
    
  )
}

export default App
