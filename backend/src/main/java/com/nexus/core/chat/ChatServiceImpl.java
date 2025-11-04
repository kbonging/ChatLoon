package com.nexus.core.chat;

import com.nexus.core.chat.dto.ChatMessageDTO;
import com.nexus.core.chat.entity.ChatMessage;
import com.nexus.core.chat.entity.ChatRoomMember;
import com.nexus.core.chat.repository.ChatMessageRepository;
import com.nexus.core.chat.repository.ChatRoomMemberRepository;
import com.nexus.core.chat.repository.ChatRoomRepository;
import com.nexus.core.chat.entity.ChatRoom;
import com.nexus.core.user.UserRepository;
import com.nexus.core.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public ChatRoom createDirectRoom(Long userIdx1, Long userIdx2) {
        // 1. 기존 1:1 방 조회
        Optional<ChatRoom> existingRoom = chatRoomRepository.findDirectRoomBetween(userIdx1, userIdx2);
        if (existingRoom.isPresent()) return existingRoom.get();

        // 2. 두 사용자 조회
        User user1 = userRepository.findById(userIdx1)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다"));
        User user2 = userRepository.findById(userIdx2)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다"));

        // 3. 새 방 생성
        ChatRoom room = new ChatRoom();
        room.setRoomType(ChatRoom.RoomType.DIRECT);
        room.setCreator(user1);
        chatRoomRepository.save(room);

        // 4. 참여자 추가
        chatRoomMemberRepository.save(new ChatRoomMember(room, user1));
        chatRoomMemberRepository.save(new ChatRoomMember(room, user2));

        return room;
    }

    @Transactional
    @Override
    public ChatMessageDTO sendDirectMessage(Long senderIdx, Long receiverIdx, String content) {
        // 1. 채팅방 확인/생성
        ChatRoom room = createDirectRoom(senderIdx, receiverIdx);

        // 2. 사용자 조회
        User sender = userRepository.findById(senderIdx)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다"));

        // 3. 메시지 엔티티 생성 및 저장
        ChatMessage message = new ChatMessage();
        message.setChatRoom(room);
        message.setSender(sender);
        message.setContent(content);
        message.setMessageType(ChatMessage.MessageType.TEXT);

        chatMessageRepository.save(message);

        // 4. 채팅방 마지막 메시지 업데이트
        room.setLastMessage(message);
        chatRoomRepository.save(room);

        // 5. DTO 생성 및 반환 (브로드캐스트에 사용)
        ChatMessageDTO messageDTO = new ChatMessageDTO();
        messageDTO.setRoomIdx(room.getRoomIdx());
        messageDTO.setSenderIdx(sender.getUserIdx());
        messageDTO.setContent(message.getContent());
        messageDTO.setMessageType(message.getMessageType().name());
        messageDTO.setMessageIdx(message.getMessageIdx()); // 저장 후 PK 사용 가능
        messageDTO.setSentAt(message.getSentAt());         // 저장 후 timestamp 사용 가능

        return messageDTO;
    }

}
