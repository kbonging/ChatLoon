package com.nexus.core.chat;

import com.nexus.core.chat.dto.ChatMessageDTO;
import com.nexus.core.chat.dto.ChatRoomListDTO;
import com.nexus.core.chat.entity.ChatRoom;
import com.nexus.core.user.dto.UserInfoDTO;

import java.util.List;

public interface ChatService {

    /**
     * 1:1 채팅방 생성
     *
     * - 두 사용자 간의 직접 대화를 위한 채팅방 생성
     * - 이미 존재하는 채팅방이 있다면 기존 방을 반환
     * - 새로운 채팅방인 경우에만 DB에 저장
     *
     * @param userIdx1 첫 번째 사용자의 고유번호 (현재 로그인 유저)
     * @param userIdx2 두 번째 사용자의 고유번호
     * @return 생성되거나 기존에 존재하는 ChatRoom 엔티티
     */
    ChatRoom createDirectRoom(Long userIdx1, Long userIdx2);

    /**
     * 1:1 메시지 전송
     *
     * - 전송된 메시지는 DB에 저장되고 실시간으로 전달
     *
     * @param senderIdx 발신자의 고유번호
     * @param receiverIdx 수신자의 고유번호
     * @param content 메시지 내용
     * @return 전송된 메시지 정보(ChatMessageDTO)
     */
    ChatMessageDTO sendDirectMessage(Long senderIdx, Long receiverIdx, String content);

    /**
     * 채팅방 내 상대방 정보 조회
     *
     * - 특정 채팅방에서 현재 사용자를 제외한 상대방의 정보 반환
     * - 1:1 채팅에서 대화 상대의 프로필 정보를 표시할 때 사용
     *
     * @param roomIdx 채팅방 고유번호
     * @param userIdx 현재 로그인한 사용자의 고유번호
     * @return 상대방의 정보(UserInfoDTO)
     */
    UserInfoDTO getReceiverInfo(Long roomIdx, Long userIdx);

    /**
     * 내 채팅방 목록 조회
     *
     * - 현재 사용자가 참여 중인 모든 채팅방 목록 반환
     * - 각 채팅방의 마지막 메시지, 읽지 않은 메시지 수 등 포함
     * - 채팅 목록 화면에서 사용
     *
     * @param userIdx 사용자 고유번호
     * @return 채팅방 목록(List<ChatRoomListDTO>)
     */
    public List<ChatRoomListDTO> getMyChatRooms(Long userIdx);

    /**
     * 특정 채팅방의 메시지 목록 조회
     *
     * - 채팅방 내의 모든 메시지를 시간순으로 조회
     * - 채팅방 입장 시 이전 대화 내역을 불러올 때 사용
     *
     * @param roomIdx 채팅방 고유번호
     * @return 메시지 목록(List<ChatMessageDTO>)
     */
    List<ChatMessageDTO> getMessagesByRoom(Long roomIdx);

//    ChatRoomListDTO getRoomSummary(Long roomIdx);
}
