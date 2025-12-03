// ProtectedRoute.jsx
import { useContext } from "react";
import { Navigate } from "react-router-dom";
import { AppContext } from "../contexts/AppContext";

export default function ProtectedRoute({ children }) {
  const {user} = useContext(AppContext);

  // 아직 AppContext에서 유저 정보를 불러오는 중이라면
  if (user === undefined) {
  return (
    <div
      className="d-flex justify-content-center align-items-center vh-100 bg-light"
      style={{ flexDirection: "column" }}
    >
      <div
        className="spinner-border text-primary mb-3"
        role="status"
        style={{ width: "4rem", height: "4rem" }}
      >
        <span className="visually-hidden">Loading...</span>
      </div>
      {/* <h5 className="text-muted fw-semibold">로딩 중입니다...</h5> */}
    </div>
  );
}


  // 로그인 안 된 상태면 로그인 페이지로 리다이렉트
  if (user === null) {
    return <Navigate to="/login" replace />;
  }

  // 로그인된 상태면 원래 컴포넌트 렌더링
  return children;
}