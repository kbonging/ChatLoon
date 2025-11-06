package com.nexus.core.chat.mapper;

import com.nexus.core.chat.dto.ChatRoomDTO;
import com.nexus.core.chat.entity.ChatMessage;
import com.nexus.core.chat.entity.ChatRoom;
import com.nexus.core.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class ChatRoomMapper {
    /** Entity → DTO 변환 */
    public static ChatRoomDTO toDTO(ChatRoom room) {
        if (room == null) return null;

        ChatRoomDTO dto = new ChatRoomDTO();
        dto.setRoomIdx(room.getRoomIdx());
        dto.setRoomName(room.getRoomName());
        dto.setRoomType(room.getRoomType().name());

        // 생성자 정보 매핑
        User creator = room.getCreator();
        if (creator != null) {
            dto.setCreatorIdx(creator.getUserIdx());
        }

        // 마지막 메시지 정보 매핑
        ChatMessage lastMessage = room.getLastMessage();
        if (lastMessage != null) {
            dto.setLastMessageIdx(lastMessage.getMessageIdx());
            dto.setLastMessageContent(lastMessage.getContent());
        }

        dto.setCreatedAt(room.getCreatedAt());
        dto.setUpdatedAt(room.getUpdatedAt());
        return dto;
    }

    /** DTO → Entity 변환 (필요 시) */
    public static ChatRoom toEntity(ChatRoomDTO dto, User creator) {
        if (dto == null) return null;

        ChatRoom room = new ChatRoom();
        room.setRoomIdx(dto.getRoomIdx());
        room.setRoomName(dto.getRoomName());
        room.setRoomType(ChatRoom.RoomType.valueOf(dto.getRoomType()));
        room.setCreator(creator);
        room.setCreatedAt(dto.getCreatedAt());
        room.setUpdatedAt(dto.getUpdatedAt());
        return room;
    }
}
