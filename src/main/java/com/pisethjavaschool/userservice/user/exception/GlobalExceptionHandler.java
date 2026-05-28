package com.pisethjavaschool.userservice.user.exception;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
@RequiredArgsConstructor
@RestControllerAdvice
@Hidden
public class GlobalExceptionHandler {
	private final ProblemDetailFactory problemFactory;

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFound(NotFoundException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problem.setTitle("Resource not found");
        problem.setType(URI.create("https://api.pisethjavaschool.com/problems/not-found"));
        return problem;
    }

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusiness(BusinessException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(exception.getStatus(), exception.getMessage());
        problem.setTitle("Business validation failed");
        problem.setType(URI.create("https://api.pisethjavaschool.com/problems/business-validation"));
        problem.setProperty("errorCode", exception.getErrorCode());
        return problem;
    }
    
    @ExceptionHandler(InvalidPinException.class)
    public Mono<ProblemDetail> handInvalidPinException(InvalidPinException exception,ServerWebExchange exchange){
    	return Mono.just(problemFactory.create(HttpStatus.BAD_REQUEST,  exception.getMessage(), ErrorCode.INVALID_PIN.name(), exchange));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ServerWebInputException.class})
    public ProblemDetail handleValidation(Exception exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Request validation failed");
        problem.setTitle("Validation error");
        problem.setProperty("errors", List.of(exception.getMessage()));
        return problem;
    }
    
    
}
