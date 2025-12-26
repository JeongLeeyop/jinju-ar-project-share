package com.community.cms.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class AlreadyJoinChannelException extends RuntimeException{

	private static final long serialVersionUID = -2114695658880773412L;
	
	public AlreadyJoinChannelException() {
        super("이미 가입된 커뮤니티 입니다.");
    }

    public AlreadyJoinChannelException(String message) {
		super(message);
	}

	public AlreadyJoinChannelException(String message, Throwable cause) {
		super(message, cause);
	}
}
