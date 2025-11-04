package com.nexus.core.chat.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatMessageDTO {
    private Long messageIdx;    // 서버에서 채우는 PK, 요청 시 무시
    private Long roomIdx;       // 채팅방 PK
    private Long senderIdx;     // 보내는 사용자 PK
    private String content;
    private String messageType;
    private LocalDateTime sentAt; // 서버에서 채우는 값
}
