package com.nexus.core.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserInfoDTO {
    private Long userIdx;
    private String userId;
    @JsonProperty("isEnabled")
    private boolean isEnabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** 권한 목록 */
    List<UserAuthDTO> authList;

    public UserInfoDTO() {
    }
}
