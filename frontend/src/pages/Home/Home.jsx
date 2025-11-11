import Navigation from "../../components/layout/Navigation";
import Sidebar from "../../components/layout/Sidebar";
import ChatMain from "../../components/layout/ChatMain";
import { useState, useEffect  } from "react";

export default function Home(){
  const [isChatOpen, setIsChatOpen] = useState(false);
  const [selectedRoom, setSelectedRoom] = useState(null); // ✅ 선택된 채팅방

  // ✅ 새로고침 시 sessionStorage에서 room 복구
  useEffect(() => {
    const savedRoom = sessionStorage.getItem("selectedRoom");
    if (savedRoom) {
      setSelectedRoom(JSON.parse(savedRoom));
      setIsChatOpen(true);
    }
  }, []);

  // ✅ Sidebar에서 방 선택 시
  const handleChatSelect = (room) => {
    setSelectedRoom(room);
    setIsChatOpen(true);
    sessionStorage.setItem("selectedRoom", JSON.stringify(room)); // 저장
  };

  // ✅ 뒤로가기 시 sessionStorage 제거
  const handleBack = () => {
    setSelectedRoom(null);
    setIsChatOpen(false);
    sessionStorage.removeItem("selectedRoom");
  };

    return (
        <>
            {/* Layout */}
            <div className="layout overflow-hidden">
              {/* Navigation */}
              <Navigation />

              {/* Sidebar */}
              <aside className={`sidebar bg-light ${isChatOpen ? "d-none d-md-block" : ""}`}>
                <Sidebar onChatSelect={handleChatSelect} />
              </aside>

              {/* ChatMain */}
              <main className={`main ${isChatOpen ? "is-visible" : ""}`}>
                <ChatMain
                  onBack={handleBack}
                  selectedRoom={selectedRoom}
                />
              </main>
            </div>

        </>
    );
}