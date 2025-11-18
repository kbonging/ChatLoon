import { useState, useEffect } from "react";
import { api } from "../../api/apiInstance"; // axios 인스턴스
import { useNavigate } from "react-router-dom";


export default function SignUp() {
  const navigate = useNavigate();
  
  const [form, setForm] = useState({
    userId: "",
    userPw: "",
    nickname: "",
    email: "",
  });

  const [errors, setErrors] = useState({});
  const [check, setCheck] = useState({
    userId: null, // true 가능, false 불가능, null 미확인
    email: null,
  });

  // 입력 핸들러
  const handleChange = (e) => {
    const { id, value } = e.target;

    setForm((prev) => ({
      ...prev,
      [id]: value,
    }));

    // 입력할 때마다 미리 reset (다시 체크 필요)
    if (id === "userId") setCheck((prev) => ({ ...prev, userId: null }));
    if (id === "email") setCheck((prev) => ({ ...prev, email: null }));
  };

  // Debounce 중복 체크 (0.5초)
  useEffect(() => {
    const timer = setTimeout(() => {
      if (form.userId.trim()) checkUserId();
    }, 500);
    return () => clearTimeout(timer);
  }, [form.userId]);

  useEffect(() => {
    const timer = setTimeout(() => {
      if (form.email.trim()) checkEmail();
    }, 500);
    return () => clearTimeout(timer);
  }, [form.email]);

  // 아이디 중복 체크
  const checkUserId = async () => {
    try {
      const res = await api.get(`/users/check-id`, {
        params: { userId: form.userId },
      });
      setCheck((prev) => ({ ...prev, userId: res.data.available }));
    } catch {
      setCheck((prev) => ({ ...prev, userId: false }));
    }
  };

  // 이메일 중복 체크
  const checkEmail = async () => {
    try {
      const res = await api.get(`/users/check-email`, {
        params: { email: form.email },
      });
      setCheck((prev) => ({ ...prev, email: res.data.available }));
    } catch {
      setCheck((prev) => ({ ...prev, email: false }));
    }
  };

  // 유효성 검사
  const validate = () => {
    const newErrors = {};

    if (!form.userId) newErrors.userId = "아이디를 입력하세요.";
    if (!form.userPw) newErrors.userPw = "비밀번호를 입력하세요.";
    if (!form.nickname) newErrors.nickname = "닉네임을 입력하세요.";
    if (!form.email) newErrors.email = "이메일을 입력하세요.";

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  // 회원가입 요청
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    const signupData = {
      userId: form.userId,
      userPw: form.userPw,
      nickname: form.nickname,
      email: form.email,
    };

    try {
        const res = await api.post("/users/signup", signupData);
        console.log("회원가입 응답 res: ", res);
        if (res.data.success) {
            alert(res.data.message);  // "회원가입 성공!"
            navigate("/login");       // 회원가입 성공 후 로그인 페이지 이동
        }
    } catch (err) {
        alert(err.response?.data?.message || "회원가입 실패");
    }
  };

  return (
    <div className="container">
      <div className="row align-items-center justify-content-center min-vh-100 gx-0">

        <div className="col-12 col-md-5 col-lg-4">
          <div className="card card-shadow border-0">
            <div className="card-body">
              <form onSubmit={handleSubmit} className="row g-6">

                <div className="col-12 text-center">
                  <h3 className="fw-bold mb-2">Sign Up</h3>
                  <p>Follow the easy steps</p>
                </div>

                {/* userId */}
                <div className="col-12">
                  <div className="form-floating">
                    <input
                      type="text"
                      className="form-control"
                      id="userId"
                      placeholder="User ID"
                      value={form.userId}
                      onChange={handleChange}
                    />
                    <label htmlFor="userId">User ID</label>
                  </div>
                  {/* 중복 체크 결과 */}
                  {check.userId === false && (
                    <div className="text-danger small mt-1">
                      이미 사용 중인 아이디입니다.
                    </div>
                  )}
                  {check.userId === true && (
                    <div className="text-success small mt-1">
                      사용 가능한 아이디입니다.
                    </div>
                  )}
                </div>

                {/* userPw */}
                <div className="col-12">
                  <div className="form-floating">
                    <input
                      type="password"
                      className="form-control"
                      id="userPw"
                      placeholder="Password"
                      value={form.userPw}
                      onChange={handleChange}
                    />
                    <label htmlFor="userPw">Password</label>
                  </div>
                  {errors.userPw && (
                    <div className="text-danger small mt-1">{errors.userPw}</div>
                  )}
                </div>

                {/* nickname */}
                <div className="col-12">
                  <div className="form-floating">
                    <input
                      type="text"
                      className="form-control"
                      id="nickname"
                      placeholder="Nickname"
                      value={form.nickname}
                      onChange={handleChange}
                    />
                    <label htmlFor="nickname">Nickname</label>
                  </div>
                  {errors.nickname && (
                    <div className="text-danger small mt-1">
                      {errors.nickname}
                    </div>
                  )}
                </div>

                {/* email */}
                <div className="col-12">
                  <div className="form-floating">
                    <input
                      type="email"
                      className="form-control"
                      id="email"
                      placeholder="Email"
                      value={form.email}
                      onChange={handleChange}
                    />
                    <label htmlFor="email">Email</label>
                  </div>
                  {/* 중복 체크 */}
                  {check.email === false && (
                    <div className="text-danger small mt-1">
                      이미 사용 중인 이메일입니다.
                    </div>
                  )}
                  {check.email === true && (
                    <div className="text-success small mt-1">
                      사용 가능한 이메일입니다.
                    </div>
                  )}
                </div>

                <div className="col-12">
                  <button className="btn btn-primary w-100" type="submit">
                    Create Account
                  </button>
                </div>

              </form>
            </div>
          </div>

          <div className="text-center mt-3">
            <p>
              Already have an account? <a href="/signin">Sign in</a>
            </p>
          </div>

        </div>
      </div>
    </div>
  );
}
