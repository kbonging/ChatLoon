import React, { useEffect, useState, useRef, useContext } from "react";
import { AppContext } from "../../contexts/AppContext";
import { useParams } from "react-router-dom";
import SockJS from "sockjs-client";
import { over } from "stompjs";
import "../../assets/css/chat.css"; // 상대경로 주의

let stompClient = null;

const ChatRoom = () => {
  const user = useContext(AppContext);
  const [connected, setConnected] = useState(false);
  const [message, setMessage] = useState("");
  const [messages, setMessages] = useState([]);
  const chatBoxRef = useRef(null);
  const { roomId } = useParams();

  /** ✅ WebSocket 연결 */
  useEffect(() => {
    connect();
    return () => disconnect(); // 언마운트 시 연결 해제
  }, []);

  const connect = () => {
    const socket = new SockJS("http://localhost:8080/ws"); // 서버 엔드포인트에 맞게 수정
    stompClient = over(socket);

    stompClient.connect({}, (frame) => {
      console.log("✅ STOMP 연결 성공:", frame);
      setConnected(true);

      // 구독 (서버에서 broadcast된 메시지를 받음)
      stompClient.subscribe(`/topic/chat/${roomId}`, (message) => {
        const body = JSON.parse(message.body);
        console.log("📩 받은 메시지:", body);
        setMessages((prev) => [...prev, body]);
      });
    });
  };

  /** 🚪 연결 해제 */
  const disconnect = () => {
    if (stompClient) {
      stompClient.disconnect(() => {
        console.log("🚪 STOMP 연결 종료됨");
        setConnected(false);
      });
    }
  };

  /** ✉️ 메시지 전송 */
  const sendMessage = () => {
    if (!user) {
      alert("로그인 정보가 없습니다.");
      return;
    }
    if (!message.trim()) return;

    const msgObj = {
      userId: user.userId, // nickname → userId
      content: message,
      roomId, // 필요 시 포함
    };

    stompClient.send(`/app/chat/${roomId}`, {}, JSON.stringify(msgObj));
    setMessage("");
  };

  /** 💬 스크롤 자동 아래로 이동 */
  useEffect(() => {
    if (chatBoxRef.current) {
      chatBoxRef.current.scrollTop = chatBoxRef.current.scrollHeight;
    }
  }, [messages]);

  return (
    <div className="chat-container">
      <h2 className="text-center">{roomId}번 채팅방</h2>

      <div className="chat-box" ref={chatBoxRef}>
        {messages.map((msg, index) => (
          <p key={index}>
            <strong>{msg.userId}:</strong> {msg.content}
          </p>
        ))}
      </div>

      <div className="input-group">
        <input
          type="text"
          className="form-control"
          placeholder="메시지를 입력하세요"
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && sendMessage()}
        />
        <button className="btn btn-primary" onClick={sendMessage}>
          전송
        </button>
      </div>

      <button
        className="btn btn-danger btn-block mt-3"
        onClick={disconnect}
        disabled={!connected}
      >
        연결 종료
      </button>
    </div>
  );
};

export default ChatRoom;
