import { Routes, Route } from "react-router-dom";
import { RequireAuth } from "react-auth-kit";
import './App.css'
import LoginScreen from "./screens/loginPage";
import HomeScreen from "./screens/homeScreen";

function App() {

  return (
    <Routes>
      <Route path="/login" element={<LoginScreen />} />

      <Route
          path="/home"
          element={
            <RequireAuth loginPath="/login">
              <HomeScreen />
            </RequireAuth>
          }
        ></Route>
    </Routes>
    
  )
}

export default App
