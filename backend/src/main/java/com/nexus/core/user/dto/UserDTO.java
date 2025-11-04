package com.nexus.core.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nexus.core.common.dto.DefaultDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class UserDTO extends DefaultDTO {
    /** 회원 고유번호 */
    private Long userIdx;
    /** 사용자 아이디  */
    private String userId;
    /** 비밀번호 (암호화 저장) */
    private String userPw;

    /** 권한 목록 */
    List<UserAuthDTO> authList;

    public UserDTO() {
    }

    public UserDTO(Long userIdx, String userId, String userPw, List<UserAuthDTO> authList) {
        this.userIdx = userIdx;
        this.userId = userId;
        this.userPw = userPw;
        this.authList = authList;
    }
}
