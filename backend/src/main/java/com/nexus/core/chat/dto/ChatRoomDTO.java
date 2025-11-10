package com.nexus.core.chat.dto;

import com.nexus.core.common.dto.DefaultDTO;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class ChatRoomDTO extends DefaultDTO {
    private Long roomIdx;              // 채팅방 고유번호
    private String roomName;           // 채팅방 이름
    private String roomType;           // DIRECT / GROUP

    private Long creatorIdx;           // 생성자 FK (user.user_idx)
    private String creatorNickname;    // 선택적 — User 닉네임 표시용

    private Long receiverIdx;           // 수신자 Idx

    private Long lastMessageIdx;       // 마지막 메시지 ID (nullable)
    private String lastMessageContent; // 마지막 메시지 내용 (nullable)
    private LocalDateTime lastMessageSendAt; // 마지막 메시지 시간 (nullable)


}
