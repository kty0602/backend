package jpabook.trello_project.domain.common.exceptions;

import jpabook.trello_project.domain.common.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler( { IllegalArgumentException.class})
    public ResponseEntity<ErrorResponseDto> IllegalArgExceptionHandler(RuntimeException e) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }


    @ExceptionHandler( { AccessDeniedException.class} )
    public ResponseEntity<ErrorResponseDto> authExceptionHandler(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponseDto(HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
    }

}
