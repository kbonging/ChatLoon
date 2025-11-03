import axios from "axios";

// Axios 기본 설정
const api = axios.create({
  baseURL: "/api",         // Vite proxy 사용 시 절대 URL 불필요
  withCredentials: true,   // 쿠키 전송
});


export {api};