// src/routes/AppRouter.jsx
import { Routes, Route } from "react-router-dom";
import Home from "../pages/Home/Home";
import Login from "../pages/Login/Login";
import Signup from "../pages/Signup/Signup";
import ChatRoom from "../pages/chat/ChatRoom";
import ChatTestPage from "../pages/chat/ChatTestPage";


export default function AppRouter() {
  return (
      <Routes>
        <Route path="/*" element={<Home />} />
        <Route path="/login" element={<Login />} />
        {/* <Route path="/signup" element={<Signup />} /> */}
        {/* <Route path="/chat/:roomIdx" element={<ChatRoom />} /> */}
        {/* <Route path="/chat/test" element={<ChatTestPage />} /> */}

      </Routes>

  );
}
