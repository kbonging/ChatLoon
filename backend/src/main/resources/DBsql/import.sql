create database ChatLoon;
use ChatLoon;

SET foreign_key_checks = 0;  -- 외래 키 체크 비활성화
TRUNCATE TABLE tb_member;    -- 테이블 데이터 삭제
SET foreign_key_checks = 1;  -- 외래 키 체크 활성화

drop database chatloon;

CREATE TABLE user (
    user_idx    BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '회원 고유번호',
    user_id    VARCHAR(60) NOT NULL UNIQUE                COMMENT '로그인 아이디',
    user_pw    VARCHAR(255) NOT NULL                      COMMENT '비밀번호 (암호화 저장)',
    is_enabled    BOOLEAN    NOT NULL DEFAULT TRUE             COMMENT '계정 활성 여부',
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP    COMMENT '가입일시',
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시'
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COMMENT='회원 기본 정보';

ALTER TABLE user
ADD COLUMN nickname VARCHAR(50) NULL COMMENT '표시 이름' AFTER user_id,
ADD COLUMN email VARCHAR(100) NULL UNIQUE COMMENT '이메일 주소' AFTER nickname,
ADD COLUMN profile_img VARCHAR(255) NULL COMMENT '프로필 이미지 URL' AFTER email;

UPDATE user
SET nickname = '김봉중', email = 'apple75391@example.com', profile_img = '/images/profile/apple.png'
WHERE user_id = 'apple75391';

UPDATE user
SET nickname = '치즈혁', email = 'choi@example.com', profile_img = '/images/profile/choi.png'
WHERE user_id = 'choi';

UPDATE user
SET nickname = '섭이', email = 'lee@example.com', profile_img = '/images/profile/lee.png'
WHERE user_id = 'lee';

UPDATE user
SET nickname = '초초초초', email = 'chchch@example.com', profile_img = '/images/profile/chchch.png'
WHERE user_id = 'chchch';

UPDATE user
SET nickname = '악마', email = 'angel@example.com', profile_img = '/images/profile/angel.png'
WHERE user_id = 'angel';


CREATE TABLE user_auth (
    auth_idx BIGINT NOT NULL AUTO_INCREMENT COMMENT '권한 고유번호',
    user_idx BIGINT NOT NULL COMMENT '회원 고유번호 (FK)',
    auth VARCHAR(50) NOT NULL COMMENT '권한명 (ex: ROLE_USER, ROLE_ADMIN)',
    PRIMARY KEY (auth_idx),
    CONSTRAINT fk_user_auth_user FOREIGN KEY (user_idx)
        REFERENCES user(user_idx)
        ON DELETE CASCADE
        ON UPDATE CASCADE
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COMMENT='회원 권한 정보';

insert into user(user_id, user_pw)
values('apple75391', '$2a$10$icZ9WU92wGzRuGJLBvWwmOWUuCtEp4vezbFUS7RUaM0C3UwuFamnS');

insert into user_auth(user_idx, auth)
values(1, 'ROLE_USER');

insert into user(user_id, user_pw)
values('choi', '$2a$10$icZ9WU92wGzRuGJLBvWwmOWUuCtEp4vezbFUS7RUaM0C3UwuFamnS');

insert into user_auth(user_idx, auth)
values(2, 'ROLE_USER');

insert into user(user_id, user_pw)
values('lee', '$2a$10$icZ9WU92wGzRuGJLBvWwmOWUuCtEp4vezbFUS7RUaM0C3UwuFamnS');

insert into user_auth(user_idx, auth)
values(3, 'ROLE_USER');

insert into user(user_id, user_pw)
values('chchch', '$2a$10$icZ9WU92wGzRuGJLBvWwmOWUuCtEp4vezbFUS7RUaM0C3UwuFamnS');

insert into user_auth(user_idx, auth)
values(4, 'ROLE_USER');

insert into user(user_id, user_pw)
values('angel', '$2a$10$icZ9WU92wGzRuGJLBvWwmOWUuCtEp4vezbFUS7RUaM0C3UwuFamnS');

insert into user_auth(user_idx, auth)
values(5, 'ROLE_USER');

SELECT
   u.user_idx, u.user_id, u.user_pw, u.is_enabled, u.created_at, u.updated_at,
   ua.auth_idx, ua.auth
FROM user u
LEFT JOIN user_auth ua ON u.user_idx = ua.user_idx
WHERE u.user_id = 'apple75391';

-- ----------------
-- 1️⃣ 채팅방 정보
CREATE TABLE chat_room (
    room_idx          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '채팅방 고유번호',
    room_name         VARCHAR(100) NULL COMMENT '채팅방 이름',
    room_type         ENUM('DIRECT', 'GROUP') NOT NULL DEFAULT 'DIRECT' COMMENT '채팅방 유형 (1:1, 그룹)',
    creator_idx       BIGINT NOT NULL COMMENT '방 생성자 (user.user_idx FK)',
    last_message_idx  BIGINT NULL COMMENT '최근 메시지 고유번호 (NULL 허용)',
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '채팅방 생성일시',
    updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    CONSTRAINT fk_chat_room_creator FOREIGN KEY (creator_idx)
        REFERENCES user(user_idx)
        ON DELETE CASCADE
        ON UPDATE CASCADE
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COMMENT='채팅방 기본 정보';


-- 2️⃣ 채팅방 참여자
CREATE TABLE chat_room_member (
    room_member_idx BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '참여자 고유번호',
    room_idx        BIGINT NOT NULL COMMENT '채팅방 고유번호',
    user_idx        BIGINT NOT NULL COMMENT '참여자 (user.user_idx FK)',
    joined_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '참여일시',
    is_active       BOOLEAN NOT NULL DEFAULT TRUE COMMENT '퇴장 여부 (FALSE 시 방 나감)',
    CONSTRAINT fk_chat_member_room FOREIGN KEY (room_idx)
        REFERENCES chat_room(room_idx)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_chat_member_user FOREIGN KEY (user_idx)
        REFERENCES user(user_idx)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    UNIQUE (room_idx, user_idx)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COMMENT='채팅방 참여자 정보';


-- 3️⃣ 채팅 메시지
CREATE TABLE chat_message (
    message_idx   BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '메시지 고유번호',
    room_idx      BIGINT NOT NULL COMMENT '채팅방 고유번호',
    sender_idx    BIGINT NOT NULL COMMENT '보낸 사용자 (user.user_idx FK)',
    content       TEXT NOT NULL COMMENT '메시지 내용',
    message_type  ENUM('TEXT','IMAGE','FILE','SYSTEM') NOT NULL DEFAULT 'TEXT' COMMENT '메시지 유형',
    sent_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '보낸 시각',
    CONSTRAINT fk_chat_message_room FOREIGN KEY (room_idx)
        REFERENCES chat_room(room_idx)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_chat_message_sender FOREIGN KEY (sender_idx)
        REFERENCES user(user_idx)
        ON DELETE CASCADE
        ON UPDATE CASCADE
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COMMENT='채팅 메시지 정보';


