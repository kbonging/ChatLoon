package com.nexus.core.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class UserInfoDTO {
    private Long userIdx;
    private String userId;
    private String nickname;
    private String email;
    private String profileImg;

    @JsonProperty("isEnabled")
    private boolean isEnabled;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** 권한 목록 */
    List<UserAuthDTO> authList;

    public UserInfoDTO() {
    }
}
