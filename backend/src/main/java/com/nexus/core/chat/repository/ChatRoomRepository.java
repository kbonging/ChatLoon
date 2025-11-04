package com.nexus.core.chat.repository;

import com.nexus.core.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // 1:1 DIRECT 방 조회 (두 사용자가 참여한 방)
    @Query("""
        SELECT cr
        FROM ChatRoom cr
        JOIN ChatRoomMember m1 ON m1.chatRoom = cr
        JOIN ChatRoomMember m2 ON m2.chatRoom = cr
        WHERE cr.roomType = com.nexus.core.chat.entity.ChatRoom.RoomType.DIRECT
          AND m1.user.userIdx = :userIdx1
          AND m2.user.userIdx = :userIdx2
    """)
    Optional<ChatRoom> findDirectRoomBetween(
            @Param("userIdx1") Long userIdx1,
            @Param("userIdx2") Long userIdx2
    );
}
