package com.nexus.core.chat;

import com.nexus.core.chat.dto.ChatMessageDTO;
import com.nexus.core.chat.entity.ChatRoom;

public interface ChatService {

    ChatRoom createDirectRoom(Long userIdx1, Long userIdx2);

    ChatMessageDTO sendDirectMessage(Long senderIdx, Long receiverIdx, String content);

}
