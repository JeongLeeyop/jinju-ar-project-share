package com.community.cms.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotVaildJoinChannelException extends RuntimeException{

	private static final long serialVersionUID = -2114695658880773412L;

    public NotVaildJoinChannelException(String message) {
		super(message);
	}

	public NotVaildJoinChannelException(String message, Throwable cause) {
		super(message, cause);
	}
}
