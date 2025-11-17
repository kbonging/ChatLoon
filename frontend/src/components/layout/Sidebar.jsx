import { useEffect, useState } from "react";
import defaultProfile from '../../assets/img/defaultProfile.png';
import { checkOrCreateChatRoom } from '../../api/chat/chatRoomApi';
import { api } from "../../api/apiInstance";
import { searchUsers } from "../../api/user/userApi";

function timeAgo(dateString) {
  const date = new Date(dateString);
  const now = new Date();
  const diff = Math.floor((now - date) / 1000);

  if (diff < 60) return `${diff}초 전`;
  if (diff < 3600) return `${Math.floor(diff / 60)}분 전`;
  if (diff < 86400) return `${Math.floor(diff / 3600)}시간 전`;
  return `${Math.floor(diff / 86400)}일 전`;
}

function formatChatTime(dateString) {
  const date = new Date(dateString);
  const now = new Date();

  const isToday =
    date.getFullYear() === now.getFullYear() &&
    date.getMonth() === now.getMonth() &&
    date.getDate() === now.getDate();

  const yesterday = new Date();
  yesterday.setDate(now.getDate() - 1);

  const isYesterday =
    date.getFullYear() === yesterday.getFullYear() &&
    date.getMonth() === yesterday.getMonth() &&
    date.getDate() === yesterday.getDate();

  if (isToday) {
    // 오늘이면 시간만 표시 (HH:MM)
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  } else if (isYesterday) {
    return "어제";
  } else {
    // 그 외 날짜는 YYYY-MM-DD 형식
    return date.toLocaleDateString();
  }
}


export default function Sidebar({ onChatSelect }) {
  const [chatRooms, setChatRooms] = useState([]);
  const [searchResult, setSearchResult] = useState([]);
  const [keyword, setKeyword] = useState("");
  const [isSearching, setIsSearching] = useState(false);

  const [isLoadingRooms, setIsLoadingRooms] = useState(true);  // 채팅방 목록 로딩
  const [isLoadingSearch, setIsLoadingSearch] = useState(false); // 사용자 검색 로딩


  // 참여중인 채팅방 목록 조회
  const fetchChatRooms = async () => {
    try {
      setIsLoadingRooms(true);
      const res = await api.get("/chat/rooms");
      setChatRooms(res.data);
    } catch (err) {
      console.error("채팅방 목록 조회 실패:", err);
    } finally {
      setIsLoadingRooms(false);
    }
  };

  useEffect(() => {
    fetchChatRooms();
  }, []);

  // 검색 입력 이벤트
  const handleSearchChange = async (e) => {
    const value = e.target.value.trim();
    setKeyword(value);

    if (value.length === 0) {
      setIsSearching(false);
      setSearchResult([]);
      fetchChatRooms();
      return;
    }

    setIsSearching(true);
    setIsLoadingSearch(true);

    try {
      const startTime = Date.now();
      const users = await searchUsers(value);

      const elapsed = Date.now() - startTime;
      const remaining = 500 - elapsed; // 유지 시간

      setTimeout(() => {
        setSearchResult(users);
        setIsLoadingSearch(false); // 🔥 여기서 로딩 종료
      }, remaining > 0 ? remaining : 0);
    } catch (err) {
      console.error("검색 실패:", err);
      setIsLoadingSearch(false);
    }
  };

  // 상대 클릭 → 채팅방 생성/이동
  const handleUserClick = async (receiverIdx) => {
    try {
      const room = await checkOrCreateChatRoom(receiverIdx);
      onChatSelect(room);
    } catch (err) {
      console.error("채팅방 생성/조회 실패:", err);
      onChatSelect(null);
    }
  };

  /** 로딩 스켈레톤 카드 */
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
      <div className="tab-content h-100">
        <div className="tab-pane fade h-100 show active">
          <div className="d-flex flex-column h-100 position-relative">
            <div className="hide-scrollbar">
              <div className="container py-8">

                {/* Title */}
                <div className="mb-8">
                  <h2 className="fw-bold m-0">Chats</h2>
                </div>

                {/* Search */}
                <div className="mb-6">
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
                      value={keyword}
                      onChange={handleSearchChange}
                    />
                  </div>
                </div>

                {/* Chat List */}
                <div className="card-list">

                {/* 🔍 검색 모드일 때 */}
                {isSearching && (
                  <>
                    {/* 검색 로딩 → 스켈레톤 5개 */}
                    {isLoadingSearch &&
                      Array(5)
                        .fill(0)
                        .map((_, idx) => <LoadingCard key={idx} />)}

                    {/* 검색 완료 */}
                    {!isLoadingSearch && (
                      <>

                        {/* 🔹 검색 결과 있을 때 → “계정 더 보기” 문구 표시 */}
                        {searchResult.length > 0 && (
                          <div className="px-3 py-2 text-muted small">
                            계정 더 보기
                          </div>
                        )}

                        {/* 검색 결과 있음 */}
                        {searchResult.length > 0 &&
                          searchResult.map((user) => (
                            <div
                              key={user.userIdx}
                              className="card border-0 text-reset"
                              onClick={() => handleUserClick(user.userIdx)}
                              style={{ cursor: "pointer" }}
                            >
                              <div className="card-body">
                                <div className="row gx-5">
                                  <div className="col-auto">
                                    <div className="avatar">
                                      <img
                                        src={defaultProfile}
                                        alt="#"
                                        className="avatar-img"
                                      />
                                    </div>
                                  </div>

                                  <div className="col">
                                    <div className="d-flex align-items-center mb-3">
                                      <h5 className="me-auto mb-0">{user.nickname}</h5>
                                    </div>
                                    <div className="text-muted extra-small">
                                      {user.userId}
                                    </div>
                                  </div>
                                </div>
                              </div>
                            </div>
                          ))}

                        {/* 검색 결과 없음 */}
                        {searchResult.length === 0 && (
                          <div className="text-center text-muted py-5">
                            No results found.
                          </div>
                        )}
                      </>
                    )}
                  </>
                )}


                  {/* 📌 일반 모드: 채팅방 목록 */}
                  {!isSearching && (
                    <>
                      {/* 채팅방 로딩 → 스켈레톤 5개 */}
                      {isLoadingRooms &&
                        Array(5)
                          .fill(0)
                          .map((_, idx) => <LoadingCard key={idx} />)}

                      {/* 채팅방 목록 표시 */}
                      {!isLoadingRooms &&
                        chatRooms.map((chat) => (
                          <div
                            key={chat.roomIdx}
                            className="card border-0 text-reset"
                            onClick={() => handleUserClick(chat.receiver.userIdx)}
                            style={{ cursor: "pointer" }}
                          >
                            <div className="card-body">
                              <div className="row gx-5">
                                <div className="col-auto">
                                  <div className="avatar">
                                    <img
                                      src={defaultProfile}
                                      alt="#"
                                      className="avatar-img"
                                    />
                                  </div>
                                </div>

                                <div className="col">
                                  <div className="d-flex align-items-center mb-3">
                                    <h5 className="me-auto mb-0">
                                      {chat.receiver.nickname}
                                    </h5>
                                    <span className="text-muted extra-small ms-2">
                                      {formatChatTime(chat.lastMessageTime)}
                                    </span>
                                  </div>

                                  <div className="d-flex align-items-center">
                                    <div className="line-clamp me-auto">
                                      {chat.lastMessage}
                                    </div>
                                  </div>
                                </div>
                              </div>
                            </div>
                          </div>
                        ))}
                    </>
                  )}

                </div>
                {/* End Chat List */}
              </div>
            </div>
          </div>
        </div>
      </div>
    </aside>
  );
}
