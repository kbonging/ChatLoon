package com.nexus.core.chat.repository;

import com.nexus.core.chat.entity.ChatMessage;
import com.nexus.core.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

//    Optional<ChatMessage> findTopByChatRoomOrderBySentAtDesc(ChatRoom chatRoom);

    // 특정 채팅방(room)에 속한 메시지를 보낸 시각(sentAt) 기준 오름차순으로 조회
    List<ChatMessage> findByChatRoomOrderBySentAtAsc(ChatRoom chatRoom);
}
