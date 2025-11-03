package com.nexus.core.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_auth")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAuth {

    /** 권한 고유 번호 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_idx")
    private Long authIdx;

    /** 사용자 (ManyToOne 연결) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx", nullable = false)
    private User user;

    /** 권한 */
    @Column(name = "auth", nullable = false, length = 50)
    private String auth;

}
