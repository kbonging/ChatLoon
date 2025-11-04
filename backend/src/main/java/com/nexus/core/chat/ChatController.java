package com.nexus.core.chat;

import com.nexus.core.chat.entity.ChatMessage;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @MessageMapping("/chat/{roomIdx}")
    @SendTo("/topic/chat/{roomIdx}")
    public ChatMessage sendMessage(@DestinationVariable String roomIdx, ChatMessage message) {
        return message;
    }
}