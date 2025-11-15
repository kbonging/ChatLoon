import { useEffect, useState } from "react";
import defaultProfile from '../../assets/img/defaultProfile.png';
import {checkOrCreateChatRoom} from '../../api/chat/chatRoomApi';
import { api } from "../../api/apiInstance";
import { searchUsers } from "../../api/user/userApi";


function timeAgo(dateString) {
  const date = new Date(dateString);
  const now = new Date();
  const diff = Math.floor((now - date) / 1000); // 초 단위 차이

  if (diff < 60) return `${diff}초 전`;
  if (diff < 3600) return `${Math.floor(diff / 60)}분 전`;
  if (diff < 86400) return `${Math.floor(diff / 3600)}시간 전`;
  return `${Math.floor(diff / 86400)}일 전`;
}

export default function Sidebar({ onChatSelect }) {
  const [chatRooms, setChatRooms] = useState([]);
  const [searchResult, setSearchResult] = useState([]);     // ⬅ 검색 결과
  const [keyword, setKeyword] = useState("");               // ⬅ 검색어
  const [isSearching, setIsSearching] = useState(false);    // ⬅ 검색모드 여부

  // 기본: 참여중인 채팅방 목록 조회
  const fetchChatRooms = async () => {
    try {
      const res = await api.get("/chat/rooms");
      console.log("fetchChatRooms ===>",res.data);
      setChatRooms(res.data); // ChatRoomListDTO 배열
    } catch (err) {
      console.error("채팅방 목록 조회 실패:", err);
    }
  };
  
  useEffect(() => {
    fetchChatRooms();
  }, []);

  // 검색 입력 시 자동 호출
  const handleSearchChange = async (e) => {
    const value = e.target.value.trim();
    setKeyword(value);

    if (value.length === 0) {
      // 검색어 비면 검색모드 종료 → 채팅방 목록 복귀
      setIsSearching(false);
      setSearchResult([]);
      return;
    }

    // 검색모드 활성화
    setIsSearching(true);

    try {
      const users = await searchUsers(value);  // 🔍 검색 API 호출
      setSearchResult(users);
    } catch (err) {
      console.error("검색 실패:", err);
    }
  };

  /** 👆 상대 클릭 시 채팅방 생성 또는 이동 */
  const handleUserClick = async (receiverIdx) => {
      try {
        const room = await checkOrCreateChatRoom(receiverIdx);
        console.log("✅ 생성 또는 조회된 채팅방:", room);
        // alert(`(확인용) 채팅방 이동: roomIdx=${room.roomIdx}`);
        onChatSelect(room); // ✅ Home.jsx로 전달 (roomIdx, receiver 정보 등)
      } catch (err) {
        console.error("❌ 채팅방 생성/조회 실패:", err);
        onChatSelect(null);
        // alert("채팅방 생성 중 오류가 발생했습니다.");
      }
    };

  // ✅ 임시 더미 채팅방 리스트
  // const dummyChats = [
  //   {  userIdx:1, name: "김봉중", lastMessage: "왤케 어려움?", time: "06:20 PM" },
  //   {  userIdx:2, name: "최재혁", lastMessage: "Hello! Yeah, I'm going to meet my friend...", time: "06:20 PM" },
  //   {  userIdx:3, name: "이창섭", lastMessage: "지금 뭐해?", time: "03:12 PM" },
  //   {  userIdx:4, name: "서은광", lastMessage: "내일 점심 어때?", time: "11:05 AM" },
  // ];

  const LoadingCard = () => (
    <div className="card border-0 text-reset">
      <div className="card-body">
        <div className="row gx-5">
          <div className="col-auto">
            <div className="avatar">
              <svg
                className="avatar-img placeholder-img"
                width="100%"
                height="100%"
                xmlns="http://www.w3.org/2000/svg"
                role="img"
                aria-label="Placeholder"
                preserveAspectRatio="xMidYMid slice"
                focusable="false"
              >
                <title>Placeholder</title>
                <rect width="100%" height="100%" fill="#868e96"></rect>
              </svg>
            </div>
          </div>

          <div className="col">
            <div className="d-flex align-items-center mb-3">
              <h5 className="placeholder-glow w-100 mb-0">
                <span className="placeholder col-5"></span>
              </h5>
            </div>

            <div className="placeholder-glow">
              <span className="placeholder col-12"></span>
              <span className="placeholder col-8"></span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );

  
  return (
    <aside className="sidebar bg-light">
      <div className="tab-content h-100" role="tablist">
        {/* Chats */}
        <div className="tab-pane fade h-100 show active" id="tab-content-chats" role="tabpanel">
          <div className="d-flex flex-column h-100 position-relative">
            <div className="hide-scrollbar">
              <div className="container py-8">
                {/* Title */}
                <div className="mb-8">
                  <h2 className="fw-bold m-0">Chats</h2>
                </div>

                {/* Search */}
                <div className="mb-6">
                  <form action="#">
                    <div className="input-group">
                      <div className="input-group-text">
                        <div className="icon icon-lg">
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
                            className="feather feather-search"
                          >
                            <circle cx="11" cy="11" r="8"></circle>
                            <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                          </svg>
                        </div>
                        
                      </div>
                      
                      <input
                        type="text"
                        className="form-control form-control-lg ps-3"
                        placeholder="Search users..."
                        aria-label="Search for messages or users..."
                        value={keyword}
                        onChange={handleSearchChange}
                      />

                    </div>
                  </form>
                </div>

                {/* Chats List */}
                <div className="card-list">
                  {/* Chat Card */}
                  {chatRooms.map((chat) => (
                  <div
                    key={chat.roomIdx}
                    className="card border-0 text-reset"
                    onClick={() => handleUserClick(chat.receiver.userIdx)} // ✅ 클릭 시 Home으로 데이터 전달
                    style={{ cursor: "pointer" }}
                  >
                    <div className="card-body">
                      <div className="row gx-5">
                        <div className="col-auto">
                          <div className="avatar">
                            <img src={defaultProfile} alt="#" className="avatar-img" />
                          </div>
                        </div>

                        <div className="col">
                          <div className="d-flex align-items-center mb-3">
                            <h5 className="me-auto mb-0">{chat.receiver.nickname}</h5>
                            <span className="text-muted extra-small ms-2">
                              {timeAgo(chat.lastMessageTime)}
                            </span>
                          </div>
                          <div className="d-flex align-items-center">
                            <div className="line-clamp me-auto">{chat.lastMessage}</div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  ))}

                  {/* Loading Placeholder Card */}
                  <div
                    className="card border-0 text-reset"
                    // style={{ cursor: "pointer", opacity: 0.5 }} // 클릭 가능하다는 느낌 + 반투명
                    onClick={() => handleUserClick(-1)} // 없는 index 전달, -1 등
                  >
                    <div className="card-body">
                      <div className="row gx-5">
                        <div className="col-auto">
                          <div className="avatar">
                            <svg
                              className="avatar-img placeholder-img"
                              width="100%"
                              height="100%"
                              xmlns="http://www.w3.org/2000/svg"
                              role="img"
                              aria-label="Placeholder"
                              preserveAspectRatio="xMidYMid slice"
                              focusable="false"
                            >
                              <title>Placeholder</title>
                              <rect width="100%" height="100%" fill="#868e96"></rect>
                            </svg>
                          </div>
                        </div>

                        <div className="col">
                          <div className="d-flex align-items-center mb-3">
                            <h5 className="placeholder-glow w-100 mb-0">
                              <span className="placeholder col-5"></span>
                            </h5>
                          </div>

                          <div className="placeholder-glow">
                            <span className="placeholder col-12"></span>
                            <span className="placeholder col-8"></span>
                          </div>
                        </div>
                      </div>
                    </div>
                  
                  </div>
                </div>
                {/* End Chats List */}
              </div>
            </div>
          </div>
        </div>
      </div>
    </aside>
  );
}
