package com.nexus.core.user.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.FetchType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /** 회원 고유번호 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_idx")
    private Long userIdx;

    /** 사용자 아이디 */
    @Column(name = "user_id", nullable = false, unique = true, length = 60)
    private String userId;

    /** 표시 이름 (닉네임) */
    @Column(name = "nickname", length = 50)
    private String nickname;

    /** 이메일 주소 */
    @Column(name = "email", unique = true, length = 100)
    private String email;

    /** 프로필 이미지 URL */
    @Column(name = "profile_img", length = 255)
    private String profileImg;

    /** 비밀번호 (암호화 저장) */
    @Column(name = "user_pw", nullable = false)
    private String userPw;

    /** 계정 활성 여부 */
    @Column(name = "is_enabled", nullable = false)
    @JsonProperty("isEnabled")
    @Builder.Default
    private Boolean isEnabled = true;

    /** 생성일시 */
    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    /** 수정일시 */
    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    /** 권한 목록 */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserAuth> authList = new ArrayList<>();
}
