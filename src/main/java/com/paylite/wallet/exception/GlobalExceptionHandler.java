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
import org.springframework.dao.DataIntegrityViolationException;



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

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleIntegrity(DataIntegrityViolationException ex)
    {
        log.warn("Data integrity violation", ex);
        return build(ErrorCode.CONFLICT, "This request conflicts with existing data.");
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
