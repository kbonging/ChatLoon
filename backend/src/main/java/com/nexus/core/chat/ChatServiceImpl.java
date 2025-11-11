package com.nexus.core.chat;

import com.nexus.core.chat.dto.ChatMessageDTO;
import com.nexus.core.chat.dto.ChatRoomListDTO;
import com.nexus.core.chat.entity.ChatMessage;
import com.nexus.core.chat.entity.ChatRoomMember;
import com.nexus.core.chat.repository.ChatMessageRepository;
import com.nexus.core.chat.repository.ChatRoomMemberRepository;
import com.nexus.core.chat.repository.ChatRoomRepository;
import com.nexus.core.chat.entity.ChatRoom;
import com.nexus.core.user.UserRepository;
import com.nexus.core.user.dto.UserInfoDTO;
import com.nexus.core.user.entity.User;
import com.nexus.core.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
        if (existingRoom.isPresent()) {
            log.info("기존 1:1 방 존재, 기존 방 반환 : {}", existingRoom.get());
            return existingRoom.get();
        };

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
        log.info("기존 1:1 방 존재하지않음 새로운 방 생성 :  {}", room);

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

    @Override
    public UserInfoDTO getReceiverInfo(Long roomIdx, Long userIdx) {
        // 1. 해당 채팅방에서 로그인 사용자를 기준으로 상대방 userIdx 조회
        Long receiverIdx = chatRoomMemberRepository.findReceiverIdx(roomIdx, userIdx);

        // 2. 만약 상대방이 없는 경우 null 반환
        if (receiverIdx == null) {
            return null;
        }

        // 3. 상대방 정보 조회 (UserInfoDTO 반환)
        User receiver = userRepository.findById(receiverIdx)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다"));

        return UserMapper.toDTO(receiver);
    }

    @Override
    public List<ChatRoomListDTO> getMyChatRooms(Long userIdx) {
        List<ChatRoom> rooms = chatRoomMemberRepository.findChatRoomsByUserIdx(userIdx);
        log.info("chatRoomMemberRepository.findChatRoomsByUserIdx(userIdx) ==> rooms {}", rooms);
        List<ChatRoomListDTO> result = new ArrayList<>();

        for(ChatRoom room : rooms){
            UserInfoDTO receiverDto = null;

            if(room.getRoomType() == ChatRoom.RoomType.DIRECT){ // 1:1 채팅일 경우
                User receiver = (User) chatRoomMemberRepository.findReceiver(room.getRoomIdx(), userIdx);
                log.info("chatRoomMemberRepository.findReceiver(room.getRoomIdx(), userIdx) ===> {}",receiver);
                if (receiver != null) {
                    receiverDto = new UserInfoDTO(
                            receiver.getUserIdx(),
                            receiver.getUserId(),
                            receiver.getNickname(),
                            receiver.getEmail(),
                            receiver.getProfileImg(),
                            receiver.getIsEnabled(),
                            receiver.getCreatedAt(),
                            receiver.getUpdatedAt(),
                            null // 권한 목록 필요 시 추가 조회 가능
                    );
                }
            }

            // 마지막 메시지 내용 및 시간 가져오기
            String lastMessage = room.getLastMessage() != null ? room.getLastMessage().getContent() : null;
            LocalDateTime lastTime = room.getLastMessage() != null ? room.getLastMessage().getSentAt() : room.getUpdatedAt();

            // ✅ DTO 객체 생성 (Builder 사용)
            ChatRoomListDTO dto = ChatRoomListDTO.builder()
                    .roomIdx(room.getRoomIdx())
                    .roomName(room.getRoomName())
                    .receiver(receiverDto)
                    .lastMessage(lastMessage)
                    .lastMessageTime(lastTime)
                    .build();

            result.add(dto);
        }

        return result;
    }

}
