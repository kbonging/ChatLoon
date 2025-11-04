package com.nexus.core.chat.entity;

import com.nexus.core.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_room_member",
        uniqueConstraints = @UniqueConstraint(columnNames = {"room_idx", "user_idx"}))
public class ChatRoomMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_Member_Idx")
    private Long roomMemberIdx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_idx", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx", nullable = false)
    private User user;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime joinedAt = LocalDateTime.now();
}
