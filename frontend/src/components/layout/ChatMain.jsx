import React, { useEffect, useState, useRef, useContext } from "react";
import defaultProfile from "../../assets/img/defaultProfile.png";
import { AppContext } from "../../contexts/AppContext";
// import { useParams } from "react-router-dom";
import SockJS from "sockjs-client";
import { over } from "stompjs";
import { api } from "../../api/apiInstance";

let stompClient = null;

export default function ChatMain({ onBack, selectedRoom }) {
  console.log("ChatMain.jsx ====> selelctedRoom : ", selectedRoom);
  const user = useContext(AppContext);
  // const { roomIdx } = useParams();
  const [receiver, setReceiver] = useState(null); // 수신자 정보
  const roomIdx = selectedRoom?.roomIdx;
  console.log("ChatMain.jsx ====> roomIdx : ", roomIdx);
  const [connected, setConnected] = useState(false);
  // const [message, setMessage] = useState("");
  const [messages, setMessages] = useState([]);
  const chatBodyRef = useRef(null);

  const messageRef = useRef();

  // selectedRoom이 바뀌면 receiver 정보 fetch
  useEffect(() => {
    if (!roomIdx) return;

    const fetchReceiverInfo = async () => {
      try {
        const res = await api.get(`/chat/rooms/${roomIdx}/receiver`);
        setReceiver(res.data);
        // console.log("fetchReceiverInfo ===>",res.data);
      } catch (err) {
        console.error("Receiver fetch error:", err);
        setReceiver(null);
      }
    };

    fetchReceiverInfo();
  }, [roomIdx]);

  useEffect(() => {
  if (!roomIdx) return;

  const fetchMessages = async () => {
    try {
      const res = await api.get(`/chat/rooms/${roomIdx}/messages`);
      setMessages(res.data); // 기존 useState(messages) 세팅
    } catch (err) {
      console.error("채팅 메시지 조회 실패:", err);
    }
  };

  fetchMessages();
}, [roomIdx]);

  /** ✅ WebSocket 연결 */
  useEffect(() => {
    if (!roomIdx) return;

    // ✅ 1. 메시지 초기화
    setMessages([]);

    // ✅ 2. 기존 구독 해제 및 연결 해제
    if (stompClient && stompClient.connected) {
      stompClient.unsubscribe(`/topic/chat/${roomIdx}`);
      stompClient.disconnect(() => {
        console.log("🚪 이전 방 연결 종료됨");
      });
    }

    // ✅ 3. 새로운 방 연결
    connect();

    // ✅ 4. cleanup - 방 이동 시 정리
    return () => {
      if (stompClient && stompClient.connected) {
        stompClient.unsubscribe(`/topic/chat/${roomIdx}`);
        stompClient.disconnect(() => {
          console.log("🧹 cleanup - 연결 종료");
        });
      }
    };
  }, [roomIdx]);


  const connect = () => {
    const socket = new SockJS("http://localhost:8080/ws");
    stompClient = over(socket);

    stompClient.connect({}, (frame) => {
      console.log("✅ STOMP 연결 성공:", frame);
      setConnected(true);

      // 구독
      stompClient.subscribe(`/topic/chat/${roomIdx}`, (message) => {
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
    const content = messageRef.current.value.trim();
    if (!content.trim()) return;

    const msgObj = {
      senderIdx: user.userIdx,          // 보낸 사람
      receiverIdx: receiver?.userIdx,   // 받는 사람
      roomIdx: selectedRoom?.roomIdx, // 채팅방
      content: content,               // 메시지 내용
      messageType: "TEXT"             // 기본 메시지 타입
    };

    stompClient.send(`/app/chat/${selectedRoom?.roomIdx}`, {}, JSON.stringify(msgObj));
    messageRef.current.value = ""; // 전송 후 초기화
  };

  /** 💬 스크롤 자동 아래로 */
  useEffect(() => {
    if (chatBodyRef.current) {
      chatBodyRef.current.scrollTop = chatBodyRef.current.scrollHeight;
    }
  }, [messages]);

  if (!selectedRoom) {
    return <div className="text-center p-5">채팅방을 선택하세요.</div>;
  }

  return (
    <main className="main is-visible" data-dropzone-area="">
      <div className="container h-100">
        <div className="d-flex flex-column h-100 position-relative">
          {/* Chat Header */}
          <div className="chat-header border-bottom py-4 py-lg-7">
            <div className="row align-items-center">
              {/* Mobile: close */}
              <div className="col-2 d-xl-none">
                <button
                  type="button"
                  className="icon icon-lg text-muted btn p-0 border-0 bg-transparent"
                  onClick={onBack}
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="24"
                    height="24"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="2"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    className="feather feather-chevron-left"
                  >
                    <polyline points="15 18 9 12 15 6"></polyline>
                  </svg>
                </button>
              </div>

              {/* Header Content */}
              <div className="col-8 col-xl-12">
                <div className="row align-items-center text-center text-xl-start">
                  {/* Title */}
                  <div className="col-12 col-xl-6">
                    <div className="row align-items-center gx-5">
                      <div className="col-auto">
                        <div className="avatar avatar-online d-none d-xl-inline-block">
                          <img
                            className="avatar-img"
                            src={defaultProfile}
                            alt=""
                          />
                        </div>
                      </div>

                      <div className="col overflow-hidden">
                        <h5 className="text-truncate">{receiver?.nickname || "Unknown"}</h5>
                        <span className="text-muted fw-light text-truncate">
                          {receiver?.userId || "Unknown"}
                        </span>
                        {/* <p className="text-truncate">
                          is typing
                          <span className="typing-dots">
                            <span>.</span>
                            <span>.</span>
                            <span>.</span>
                          </span>
                        </p> */}
                      </div>
                    </div>
                  </div>

                  {/* Toolbar */}
                  <div className="col-xl-6 d-none d-xl-block">
                    <div className="row align-items-center justify-content-end gx-6">
                      <div className="col-auto">
                        <a
                          href="#"
                          className="icon icon-lg text-muted"
                          data-bs-toggle="offcanvas"
                          data-bs-target="#offcanvas-more"
                          aria-controls="offcanvas-more"
                        >
                          <svg
                            xmlns="http://www.w3.org/2000/svg"
                            width="24"
                            height="24"
                            viewBox="0 0 24 24"
                            fill="none"
                            stroke="currentColor"
                            strokeWidth="2"
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            className="feather feather-more-horizontal"
                          >
                            <circle cx="12" cy="12" r="1"></circle>
                            <circle cx="19" cy="12" r="1"></circle>
                            <circle cx="5" cy="12" r="1"></circle>
                          </svg>
                        </a>
                      </div>

                      <div className="col-auto">
                        <div className="avatar-group">
                          <a
                            href="#"
                            className="avatar avatar-sm"
                            data-bs-toggle="modal"
                            data-bs-target="#modal-user-profile"
                          >
                            <img
                              className="avatar-img"
                              src={defaultProfile}
                              alt="#"
                            />
                          </a>

                          <a
                            href="#"
                            className="avatar avatar-sm"
                            data-bs-toggle="modal"
                            data-bs-target="#modal-profile"
                          >
                            <img
                              className="avatar-img"
                              src={defaultProfile}
                              alt="#"
                            />
                          </a>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              {/* Mobile: more */}
              <div className="col-2 d-xl-none text-end">
                <a
                  href="#"
                  className="icon icon-lg text-muted"
                  data-bs-toggle="offcanvas"
                  data-bs-target="#offcanvas-more"
                  aria-controls="offcanvas-more"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="24"
                    height="24"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="2"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    className="feather feather-more-vertical"
                  >
                    <circle cx="12" cy="12" r="1"></circle>
                    <circle cx="12" cy="5" r="1"></circle>
                    <circle cx="12" cy="19" r="1"></circle>
                  </svg>
                </a>
              </div>
            </div>
          </div>

          {/* Chat Content */}
          <div 
            className="chat-body hide-scrollbar flex-1 h-100" 
            ref={chatBodyRef}
            style={{ paddingBottom: "90px" }}
          >
            <div className="chat-body-inner">
              <div className="py-6 py-lg-12">
                {messages.map((msg, index) => {
                  // console.log("******************",msg);
                  const isMine = msg.senderIdx === user.userIdx;
                  return (
                    <div
                      key={index}
                      className={isMine ? "message message-out" : "message"}
                      // className="message message-out"
                    >
                      <a
                        href="#"
                        data-bs-toggle="modal"
                        className="avatar avatar-responsive"
                      >
                        <img className="avatar-img" src={defaultProfile} alt="" />
                      </a>
                      <div className="message-inner">
                        <div className="message-body">
                          <div className="message-content">
                            <div className="message-text">
                              <p>{msg.content}</p>
                            </div>
                          </div>
                        </div>
                        <div className="message-footer">
                          <span className="extra-small text-muted">
                            {new Date(msg.sentAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                          </span>
                        </div>
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          </div>

          {/* Chat Footer */}
          <div className="chat-footer pb-3 pb-lg-7 position-absolute bottom-0 start-0">
            <div
              className="dz-preview bg-dark"
              id="dz-preview-row"
              data-horizontal-scroll=""
            ></div>
            
            <form
              className="chat-form rounded-pill bg-dark" data-emoji-form=""
              onSubmit={(e) => {
                e.preventDefault();
                sendMessage();
              }}
            >
              <div className="row align-items-center gx-0">
                 <div className="col-auto">
                  <a
                    href="#"
                    className="btn btn-icon btn-link text-body rounded-circle"
                    id="dz-btn"
                  >
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      width="24"
                      height="24"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      strokeWidth="2"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      className="feather feather-paperclip"
                    >
                      <path d="M21.44 11.05l-9.19 9.19a6 6 0 0 1-8.49-8.49l9.19-9.19a4 4 0 0 1 5.66 5.66l-9.2 9.19a2 2 0 0 1-2.83-2.83l8.49-8.48"></path>
                    </svg>
                  </a>
                </div>
                <div className="col">
                  <div className="input-group">
                    <textarea
                      className="form-control px-0"
                      placeholder="Type your message..."
                      rows="1"
                      ref={messageRef}
                      // onChange={(e) => setMessage(e.target.value)}
                      // onKeyDown={(e) => e.key === "Enter" && !e.shiftKey && (e.preventDefault(), sendMessage())}
                      onKeyDown={(e) => {
                        if (e.key === "Enter" && !e.shiftKey) {
                          e.preventDefault();
                          sendMessage(messageRef.current.value);
                          messageRef.current.value = ""; // 전송 후 초기화
                        }
                      }}
                    ></textarea>
                    <a href="#" className="input-group-text text-body pe-0">
                      <span className="icon icon-lg">
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          width="24"
                          height="24"
                          viewBox="0 0 24 24"
                          fill="none"
                          stroke="currentColor"
                          strokeWidth="2"
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          className="feather feather-smile"
                        >
                          <circle cx="12" cy="12" r="10"></circle>
                          <path d="M8 14s1.5 2 4 2 4-2 4-2"></path>
                          <line x1="9" y1="9" x2="9.01" y2="9"></line>
                          <line x1="15" y1="9" x2="15.01" y2="9"></line>
                        </svg>
                      </span>
                    </a>
                  </div>
                </div>
                <div className="col-auto">
                  <button
                    type="submit"
                    className="btn btn-icon btn-primary rounded-circle ms-5"
                  >
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      width="24"
                      height="24"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      strokeWidth="2"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      className="feather feather-send"
                    >
                      <line x1="22" y1="2" x2="11" y2="13"></line>
                      <polygon points="22 2 15 22 11 13 2 9 22 2"></polygon>
                    </svg>
                  </button>
                </div>
              </div>
            </form>
          </div>
        </div>
      </div>
    </main>
  );
}
