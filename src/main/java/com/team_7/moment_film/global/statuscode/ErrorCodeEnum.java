package com.team_7.moment_film.global.statuscode;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public enum ErrorCodeEnum {

    TOKEN_INVALID(HttpStatus.BAD_REQUEST, "유효한 토큰이 아닙니다."),
    TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "토큰이 만료되었습니다."),
    LOGIN_FAIL(HttpStatus.BAD_REQUEST, "로그인 실패."),
    DUPLICATE_USERNAME_EXIST(HttpStatus.BAD_REQUEST, "중복된 사용자가 존재합니다."),
    USER_NOT_MATCH(HttpStatus.BAD_REQUEST, "작성자만 수정, 삭제가 가능합니다."),
    POST_NOT_EXIST(HttpStatus.BAD_REQUEST, "존재하지 않는 게시글입니다."),
    CATEGORY_NOT_EXIST(HttpStatus.BAD_REQUEST, "존재하지 않는 카테고리입니다."),
    COMMENT_NOT_EXIST(HttpStatus.BAD_REQUEST, "존재하지 않는 댓글입니다."),
    FILE_INVALID(HttpStatus.BAD_REQUEST, "유효한 파일이 아닙니다."),
    FILE_DECODE_FAIL(HttpStatus.BAD_REQUEST, "파일 이름 디코딩에 실패했습니다."),
    URL_INVALID(HttpStatus.BAD_REQUEST, "잘못된 URL 형식입니다."),
    EXTRACT_INVALID(HttpStatus.BAD_REQUEST, "확장자를 추출할 수 없습니다."),
    UPLOAD_FAIL(HttpStatus.BAD_REQUEST, "유효하지 않은 요청입니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCodeEnum(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

}
