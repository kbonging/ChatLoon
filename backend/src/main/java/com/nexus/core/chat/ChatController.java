package com.nexus.core.chat;

import com.nexus.core.chat.dto.ChatRoomDTO;
import com.nexus.core.chat.entity.ChatMessage;
import com.nexus.core.chat.entity.ChatRoom;
import com.nexus.core.chat.mapper.ChatRoomMapper;
import com.nexus.core.security.custom.CustomUser;
import com.nexus.core.user.dto.UserInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService chatService;
    private final ChatRoomMapper chatRoomMapper;

    // ✅ STOMP 메시지 처리
    @MessageMapping("/chat/{roomIdx}")
    @SendTo("/topic/chat/{roomIdx}")
    public ChatMessage sendMessage(@DestinationVariable String roomIdx, ChatMessage message) {
        System.out.println(message.toString());
        log.info("/topic/chat/roomIdx:{} - message : {}", roomIdx, message);
        return message;
    }

    /**
     * 채팅방 생성 or 조회
     * */
    @PostMapping("/rooms/check-or-create")
    public ResponseEntity<?> checkOrCreateRoom(
            @AuthenticationPrincipal CustomUser customUser,
            @RequestBody Map<String, Long> request) {
        Long receiverIdx = request.get("receiverIdx");

        ChatRoom room = chatService.createDirectRoom(customUser.getUser().getUserIdx(), receiverIdx);
        ChatRoomDTO chatRoomDTO = chatRoomMapper.toDTO(room);
        chatRoomDTO.setReceiverIdx(receiverIdx);
        log.info("✅ 채팅방 생성/조회 완료: {}", chatRoomDTO);
        return ResponseEntity.ok(chatRoomDTO);
    }

    /** 
     * 해당 채팅방 수신자 정보 조회
     * */
    @GetMapping("/rooms/{roomIdx}/receiver")
    public ResponseEntity<?> getReceiverInfo(@PathVariable Long roomIdx,
                                             @AuthenticationPrincipal CustomUser customUser) {
        UserInfoDTO receiver = chatService.getReceiverInfo(roomIdx, customUser.getUser().getUserIdx());
        if (receiver == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Receiver not found");
        }
        return ResponseEntity.ok(receiver);
    }

}