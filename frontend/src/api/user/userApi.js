// frontend/api/user/userApi.js
import { api } from "../apiInstance";

// 🔍 사용자 검색 API
export const searchUsers = async (keyword) => {
  const res = await api.get("/users/search", {
    params: { keyword },
  });
  return res.data;
};
