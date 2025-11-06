package com.nexus.core.chat.entity;

import com.nexus.core.chat.dto.ChatRoomDTO;
import com.nexus.core.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_room")
@Getter
@Setter
@ToString
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_Idx")
    private Long roomIdx;

    @Column(name = "room_name", nullable = true, length = 100)
    private String roomName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType roomType = RoomType.DIRECT; // DIRECT or GROUP

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_idx", nullable = false)
    private User creator;  // FK → user.user_idx

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_message_idx", referencedColumnName = "message_Idx")
    private ChatMessage lastMessage; // NULL 허용

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum RoomType {
        DIRECT, GROUP
    }

}
