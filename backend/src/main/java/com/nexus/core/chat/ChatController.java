package com.nexus.core.chat;

import com.nexus.core.chat.dto.ChatMessageDTO;
import com.nexus.core.chat.dto.ChatRoomDTO;
import com.nexus.core.chat.dto.ChatRoomListDTO;
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
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService chatService;
    private final ChatRoomMapper chatRoomMapper;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * STOMP 메시지 처리
     */
    @MessageMapping("/chat/{roomIdx}")
    @SendTo("/topic/chat/{roomIdx}")
    public ChatMessageDTO sendMessage(@DestinationVariable String roomIdx,
                                      @Payload Map<String, Object> messageData) {
        log.info("/topic/chat/roomIdx:{} - messageData : {}", roomIdx, messageData);

        // 1. 프론트에서 전달된 데이터 파싱
        Long senderIdx = Long.parseLong(String.valueOf(messageData.get("senderIdx")));
        Long receiverIdx = Long.parseLong(String.valueOf(messageData.get("receiverIdx")));
        String content = (String) messageData.get("content");

        // 2. 메시지 저장
        ChatMessageDTO savedMessage = chatService.sendDirectMessage(senderIdx, receiverIdx, content);
        log.info("Message Saved and Broadcasting: {}", savedMessage);


        return savedMessage;
    }

    /**
     * 로그인한 사용자가 참여 중인 채팅방 목록 조회
     */
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomListDTO>> getMyChatRooms(
            @AuthenticationPrincipal CustomUser customUser
    ) {
        Long userIdx = customUser.getUser().getUserIdx();
        List<ChatRoomListDTO> rooms = chatService.getMyChatRooms(userIdx);
        log.info("rooms : {}", rooms);
        return ResponseEntity.ok(rooms);
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

    /**
     * 채팅방 메시지 목록 조회
     * */
    @GetMapping("/rooms/{roomIdx}/messages")
    public List<ChatMessageDTO> getChatMessages(@PathVariable Long roomIdx) {
        return chatService.getMessagesByRoom(roomIdx);
    }

}