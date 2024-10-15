package jpabook.trello_project.domain.common.exceptions;

import jpabook.trello_project.domain.common.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

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

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponseDto> handleCustomException(ApiException e) {
        BaseErrorStatus errorStatus = e.getErrorStatus();
        return ResponseEntity.status(errorStatus.getHttpStatus())
                .body(errorStatus.getErrorStatus());
    }

    @ExceptionHandler(InvalidTitleException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidTitleException(InvalidTitleException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    // method argument resolver 에서 validation 예외 발생시
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleBindException(MethodArgumentNotValidException ex) {
        String errorCodes = ex.getBindingResult().getAllErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(","));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto(HttpStatus.BAD_REQUEST, errorCodes));
    }
}
