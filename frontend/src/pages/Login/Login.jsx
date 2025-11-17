import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { login } from "../../api/auth";

import "../../assets/css/template.bundle.css";
import "../../assets/css/template.dark.bundle.css";

export default function Login() {
  const [userId, setUserId] = useState("");
  const [userPw, setUserPw] = useState("");
  const [error, setError] = useState(null); // 로그인 실패 메시지
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);

    if (!userId || !userPw) {
      setError("아이디와 비밀번호를 입력해주세요.");
      return;
    }

    try {
      const data = await login(userId, userPw);
      console.log("로그인 성공:", data);

      // 로그인 성공 후 이동 (예: DM 페이지)
      navigate("/home"); 
    } catch (err) {
      console.error("로그인 실패:", err);
      setError(err.response?.data?.message || "아이디 또는 비밀번호가 잘못 되었습니다. 아이디와 비밀번호를 정확히 입력해 주세요.");
    }
  };

  return (
    <div className="bg-light">
      <div className="container">
        <div className="row align-items-center justify-content-center min-vh-100 gx-0">
          <div className="col-12 col-md-5 col-lg-4">
            <div className="card card-shadow border-0">
              <div className="card-body">
                <div className="row g-6">
                  <div className="col-12 text-center">
                    <h3 className="fw-bold mb-2">Sign In</h3>
                    <p>Login to your account</p>
                  </div>

                  {error && (
                    <div className="col-12">
                      <div className="alert alert-danger">{error}</div>
                    </div>
                  )}

                  <div className="col-12">
                    <div className="form-floating">
                      <input
                        type="text"
                        className="form-control"
                        id="signin-userId"
                        placeholder="User ID"
                        value={userId}
                        onChange={(e) => setUserId(e.target.value)}
                      />
                      <label htmlFor="signin-userId">User ID</label>
                    </div>
                  </div>

                  <div className="col-12">
                    <div className="form-floating">
                      <input
                        type="password"
                        className="form-control"
                        id="signin-password"
                        placeholder="Password"
                        value={userPw}
                        onChange={(e) => setUserPw(e.target.value)}
                      />
                      <label htmlFor="signin-password">Password</label>
                    </div>
                  </div>

                  <div className="col-12">
                    <button
                      className="btn btn-block btn-lg btn-primary w-100"
                      type="submit"
                      onClick={handleSubmit}
                    >
                      Sign In
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <div className="text-center mt-8">
              <p>
                Don't have an account yet? <Link to="/signup">Sign up</Link>
              </p>
              <p>
                <Link to="/password-reset">Forgot Password?</Link>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
