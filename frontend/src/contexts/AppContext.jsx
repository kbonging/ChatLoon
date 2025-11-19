import { createContext, useState, useEffect } from "react";
import Cookies from "js-cookie";
import { api } from "../api/apiInstance"; 
import { useNavigate } from "react-router-dom";

export const AppContext = createContext();

export const AppProvider = ({ children }) => {
  const [user, setUser] = useState(undefined); // undefined = 아직 로딩 안 됨
  const navigate = useNavigate();

  const fetchUser = async () => {
    try {
      const res = await api.get("/users/info");
      setUser(res.data);
    } catch (err) {
      console.error("사용자 정보 가져오기 실패:", err);
      setUser(null); // 로그인 안 된 상태
    }
  };

  useEffect(() => {
    fetchUser();
  }, []);

  return (
    <AppContext.Provider value={{user, setUser, fetchUser}}>
      {children}
    </AppContext.Provider>
  );
};