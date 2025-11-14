package com.nexus.core.user;

import com.nexus.core.user.dto.UserDTO;
import com.nexus.core.user.dto.UserInfoDTO;

import java.util.List;

public interface UserService {

    /**
     * 회원 정보 조회 (PK 기준)
     * - 추후 다른 프로필 테이블과의 조인을 고려해 확장 예정
     *
     * @param userIdx 회원 고유번호 (PK)
     * @return 회원 엔티티(Optional)
     */
    UserInfoDTO getUserInfo(Long userIdx);

    /**
     * 키워드를 기반으로 회원 목록을 조회
     *
     * @param keyword 검색 키워드 (이름, 아이디 등)
     * @return 조건에 부합하는 회원 목록
     */
    public List<UserInfoDTO> searchUsers(String keyword);
}
