import Navigation from "../../components/layout/Navigation";
import Sidebar from "../../components/layout/Sidebar";
import ChatMain from "../../components/layout/ChatMain";
import { useState  } from "react";

export default function Home(){
  const [isChatOpen, setIsChatOpen] = useState(true);
  const [selectedRoom, setSelectedRoom] = useState(null); // ✅ 선택된 채팅방

    return (
        <>
            {/* Layout */}
            <div className="layout overflow-hidden">
              {/* Navigation */}
              <Navigation />

              {/* Sidebar */}
              <aside className={`sidebar bg-light ${isChatOpen ? "d-none d-md-block" : ""}`}>
                <Sidebar
                  onChatSelect={(room) => {
                    setSelectedRoom(room);   // roomIdx, receiver 정보 등 저장
                    setIsChatOpen(true);     // 채팅 화면으로 전환
                  }}
                />
              </aside>

              {/* ChatMain */}
              <main className={`main ${isChatOpen ? "is-visible" : ""}`}>
                <ChatMain
                  onBack={() => setIsChatOpen(false)}
                  selectedRoom={selectedRoom}  // ✅ 전달
                />
              </main>
            </div>

        </>
    );
}