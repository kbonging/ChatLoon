
import { api } from "./apiInstance"; 

// -----------------------------
// 로그인 API
// -----------------------------
export const login = async (userId, userPw) => {
  const response = await api.post("/auth/login", { userId, userPw });
  return response.data;
};
