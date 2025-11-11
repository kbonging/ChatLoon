package com.nexus.core.chat.repository;

import com.nexus.core.chat.entity.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

    /**
     * 1:1 채팅방에서 로그인한 사용자를 기준으로 상대방(userIdx)을 조회
     *
     * @param roomIdx      조회할 채팅방 ID
     * @param loginUserIdx 로그인한 사용자의 userIdx
     * @return 상대방의 userIdx (없으면 null)
     *
     * 동작 설명:
     * 1. m1: 로그인 사용자의 ChatRoomMember 엔티티
     * 2. m2: 같은 방에 속한 다른 ChatRoomMember 엔티티 (상대방)
     * 3. JOIN 조건: m1과 m2가 같은 채팅방(roomIdx)에 속해야 함
     * 4. WHERE 조건:
     *    - m1이 로그인한 사용자인지 확인
     *    - m2가 로그인한 사용자가 아닌지 확인
     * 5. SELECT: m2.user.userIdx 반환 → 채팅방 상대방 식별
     */
    @Query("""
        SELECT m2.user.userIdx
        FROM ChatRoomMember m1
        JOIN ChatRoomMember m2
          ON m1.chatRoom.roomIdx = m2.chatRoom.roomIdx
        WHERE m1.chatRoom.roomIdx = :roomIdx
          AND m1.user.userIdx = :loginUserIdx
          AND m2.user.userIdx <> :loginUserIdx
    """)
    Long findReceiverIdx(@Param("roomIdx") Long roomIdx,
                         @Param("loginUserIdx") Long loginUserIdx);
}
