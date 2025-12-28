package com.community.cms.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class DomainDuplicateException extends RuntimeException{

	private static final long serialVersionUID = -2114695658880773412L;
	
	public DomainDuplicateException() {
        super("이미 사용중인 도메인 입니다.");
    }

    public DomainDuplicateException(String message) {
		super(message);
	}

	public DomainDuplicateException(String message, Throwable cause) {
		super(message, cause);
	}
}
