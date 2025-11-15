// src/routes/AppRouter.jsx
import { Routes, Route } from "react-router-dom";
import Home from "../pages/Home/Home";
import Login from "../pages/Login/Login";
import Signup from "../pages/Signup/Signup";
import ChatRoom from "../pages/chat/ChatRoom";
import ChatTestPage from "../pages/chat/ChatTestPage";

import ProtectedRoute from "./ProtectedRoute";


export default function AppRouter() {
  return (
      <Routes>
        {/* <Route path="/home" element={<Home />} /> */}

        {/* 보호가 필요한 페이지는 ProtectedRoute로 감싸기 */}
        <Route
          path="/home"
          element={
            <ProtectedRoute>
              <Home />
            </ProtectedRoute>
          }
        />

        <Route
          path="/"
          element={
            <ProtectedRoute>
              <Home />
            </ProtectedRoute>
          }
        />

        {/* 로그인 페이지는 보호하지 않음 */}
        <Route path="/login" element={<Login />} />
        {/* <Route path="/signup" element={<Signup />} /> */}
        {/* <Route path="/chat/:roomIdx" element={<ChatRoom />} /> */}
        <Route path="/chat/test" element={<ChatTestPage />} />

      </Routes>

  );
}
