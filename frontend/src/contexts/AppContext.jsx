import { createContext, useState, useEffect } from "react";
import Cookies from "js-cookie";

import { api } from "../api/apiInstance"; 

export const AppContext = createContext();

export const AppProvider = ({ children }) => {
  const [user, setUser] = useState(null);

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const res = await api.get("/users/info");
        console.log(res);
        
        setUser(res.data);
        console.log("현재 로그인 사용자 정보 : ", res.data);
      } catch (err) {
        console.error("사용자 정보 가져오기 실패:", err);
      }
    };
    fetchUser();
  }, []);

  return <AppContext.Provider value={user}>{children}</AppContext.Provider>;
};