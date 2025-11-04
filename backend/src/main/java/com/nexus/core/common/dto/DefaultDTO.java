package com.nexus.core.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class DefaultDTO {
    /** 검색조건 */
    private String searchCondition;
    /** 검색Keyword */
    private String searchKeyword;
    /** 생성일시 */
    private LocalDateTime createdAt;
    /** 수정일시 */
    private LocalDateTime updatedAt;
    /** 계정 활성 여부 */
    @JsonProperty("isEnabled")
    private Boolean isEnabled;

    /** 현재 페이지 번호 (기본 1)*/
//    private int page = 1;
    /** 조회 시작 인덱스 */
//    private int firstIndex;
    /** 한 페이지당 조회 레코드 수 */
//    private int recordCount;


}