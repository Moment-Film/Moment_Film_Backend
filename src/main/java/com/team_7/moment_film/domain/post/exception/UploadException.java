package com.team_7.moment_film.domain.post.exception;

import com.team_7.moment_film.global.statuscode.ErrorCodeEnum;

public class UploadException extends RuntimeException{
    ErrorCodeEnum errorCodeEnum;

    public UploadException(ErrorCodeEnum errorCodeEnum) {
        this.errorCodeEnum = errorCodeEnum;
    }

    public UploadException(ErrorCodeEnum errorCodeEnum, Throwable cause) {
        super(cause);
        this.errorCodeEnum = errorCodeEnum;
    }
}
