import { api } from "../apiInstance";

/**
 * 💬 채팅방 존재 여부 확인 + 없을 시 생성
 * @param {number} receiverIdx - 채팅을 시작할 상대방의 회원 번호
 * @returns {Promise<object>} - 생성 또는 조회된 채팅방 정보 (roomIdx 등)
 */
export const checkOrCreateChatRoom = async (receiverIdx) => {
  try {
    const response = await api.post("/chat/rooms/check-or-create", {
      receiverIdx,
    });
    console.log("checkOrCreateChatRoom() 호출 ===> response.data : ", response.data);
    return response.data; // ex) { roomIdx: 5, roomType: "DIRECT", ... }
  } catch (error) {
    console.error("❌ 채팅방 생성/조회 중 오류:", error);
    throw error;
  }
};
