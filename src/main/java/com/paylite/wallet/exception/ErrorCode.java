package com.paylite.wallet.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
         VALIDATION_FAILED(HttpStatus.BAD_REQUEST,"Validation failed"),
         UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"Authentication required"),
         FORBIDDEN(HttpStatus.FORBIDDEN,"Access denied"),
         NOT_FOUND(HttpStatus.NOT_FOUND,"Resource not found"),
         CONFLICT(HttpStatus.CONFLICT,"Conflict"),
         INSUFFICIENT_BALANCE(HttpStatus.UNPROCESSABLE_ENTITY,"Insufficient balance"),
         TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS,"Too many requests"),
         INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"Something went wrong") ;


         private final HttpStatus status;
         private final String title;

         ErrorCode(HttpStatus status, String title)
         {
             this.status=status;
             this.title=title;
         }

         public HttpStatus getStatus()
         {
             return status;
         }

         public String getTitle()
         {
             return title;
         }

}
