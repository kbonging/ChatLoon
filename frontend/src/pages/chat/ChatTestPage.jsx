import React from "react";
import { checkOrCreateChatRoom } from "../../api/chat/chatRoomApi";
import { useNavigate } from "react-router-dom";

const ChatTestPage = () => {
  const navigate = useNavigate();

  // 🧍‍♀️ 테스트용 더미 유저 리스트 (receiverIdx만 있으면 충분)
  const dummyUsers = [
    { id: 2, name: "김철수" },
    { id: 3, name: "이영희" },
    { id: 4, name: "박민수" },
  ];

  /** 👆 상대 클릭 시 채팅방 생성 또는 이동 */
  const handleUserClick = async (receiverIdx) => {
    try {
      const room = await checkOrCreateChatRoom(receiverIdx);
      console.log("✅ 생성 또는 조회된 채팅방:", room);
      alert(`채팅방 이동: roomIdx=${room.roomIdx}`);
      navigate(`/chat/${room.roomIdx}`); // 채팅방으로 이동
    } catch (err) {
      console.error("❌ 채팅방 생성/조회 실패:", err);
      alert("채팅방 생성 중 오류가 발생했습니다.");
    }
  };

  return (
    <div style={{ padding: "40px" }}>
      <h2>💬 채팅방 생성 테스트 페이지</h2>
      <p>상대방을 클릭하면 채팅방이 자동 생성됩니다.</p>

      <ul style={{ listStyle: "none", padding: 0 }}>
        {dummyUsers.map((user) => (
          <li
            key={user.id}
            style={{
              border: "1px solid #ddd",
              borderRadius: "8px",
              padding: "12px",
              marginBottom: "10px",
              cursor: "pointer",
              backgroundColor: "#f9f9f9",
              transition: "background-color 0.2s ease",
            }}
            onClick={() => handleUserClick(user.id)}
            onMouseEnter={(e) => (e.currentTarget.style.backgroundColor = "#eef")}
            onMouseLeave={(e) => (e.currentTarget.style.backgroundColor = "#f9f9f9")}
          >
            👤 {user.name} (receiverIdx: {user.id})
          </li>
        ))}
      </ul>
    </div>
  );
};

export default ChatTestPage;
