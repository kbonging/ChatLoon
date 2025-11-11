package com.nexus.core.chat.dto;

import com.nexus.core.user.dto.UserInfoDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ChatRoomListDTO {
    private Long roomIdx;
    private String roomName;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private UserInfoDTO receiver; // Direct 채팅 시 상대 정보

}
