package com.pisethjavaschool.userservice.user.exception;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
@Component
public class DefaultFactory implements ProblemDetailFactory {

	@Override
	public ProblemDetail create(HttpStatus status, String message, ServerWebExchange exchange) {
		return create(status,message, status.name(),exchange);
	}

	@Override
	public ProblemDetail create(HttpStatus status, String message, ServerWebExchange exchange,
			Map<String, Object> properties) {
		ProblemDetail pd = create(status,message,exchange);
		if(Objects.nonNull(properties)) {
			properties.forEach(pd::setProperty);
		}
		
		return pd;
	}

	@Override
	public ProblemDetail create(HttpStatus status, String message, String errorCode, ServerWebExchange exchange) {
		ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, message);
		pd.setTitle(status.getReasonPhrase());
		pd.setProperty("timestamp", Instant.now());
		pd.setProperty("path", exchange.getRequest().getPath().value());
		pd.setProperty("errorCode", errorCode);
		pd.setProperty("service", "room-service");
		return pd;
	}

}
