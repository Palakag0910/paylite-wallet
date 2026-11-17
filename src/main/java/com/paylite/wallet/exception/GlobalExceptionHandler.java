package com.paylite.wallet.exception;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;



import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(ApiException.class)
    public  ResponseEntity<ProblemDetail> handleApi(ApiException ex)
    {
        return build(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex) {
        List<String> problems = ex.getBindingResult().getFieldErrors().stream().map(f -> f.getField() + ": " + f.getDefaultMessage()).toList();
        ResponseEntity<ProblemDetail> response = build(ErrorCode.VALIDATION_FAILED, "Some fields are invalid. ");
        response.getBody().setProperty("errors", problems);
        return response;
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleAny(Exception ex)
    {
        log.error("Unexpected error",ex);
        return build(ErrorCode.INTERNAL_ERROR, "An unexpected error occurred. ");
    }

    private  ResponseEntity<ProblemDetail> build(ErrorCode code, String detail)
    {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(code.getStatus(), detail);
        problem.setTitle(code.getTitle());
        problem.setProperty("code", code.name());
        return ResponseEntity.status(code.getStatus()).contentType(MediaType.APPLICATION_PROBLEM_JSON).body(problem);
    }
}
