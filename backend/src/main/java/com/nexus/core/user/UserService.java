package com.nexus.core.user;

import com.nexus.core.user.dto.UserInfoDTO;
import com.nexus.core.user.entity.User;

import java.util.Optional;

public interface UserService {

    /**
     * 회원 정보 조회 (PK 기준)
     * - 추후 다른 프로필 테이블과의 조인을 고려해 확장 예정
     *
     * @param userIdx 회원 고유번호 (PK)
     * @return 회원 엔티티(Optional)
     */
    UserInfoDTO getUserInfo(Long userIdx);
}
