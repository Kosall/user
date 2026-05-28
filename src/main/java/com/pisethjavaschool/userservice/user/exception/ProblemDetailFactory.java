package com.pisethjavaschool.userservice.user.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
@Component
public interface ProblemDetailFactory {
	
	ProblemDetail create(HttpStatus status, String message, ServerWebExchange exchange);
	
	ProblemDetail create(HttpStatus status, String message, ServerWebExchange exchange, Map<String, Object> properties);

	ProblemDetail create(HttpStatus status, String message, String errorCode, ServerWebExchange exchange);
}
