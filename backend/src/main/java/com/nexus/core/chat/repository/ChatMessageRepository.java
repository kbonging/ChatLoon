package com.nexus.core.chat.repository;

import com.nexus.core.chat.entity.ChatMessage;
import com.nexus.core.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Optional<ChatMessage> findTopByChatRoomOrderBySentAtDesc(ChatRoom chatRoom);
}
