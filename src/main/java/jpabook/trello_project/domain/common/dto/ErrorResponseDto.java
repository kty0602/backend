package jpabook.trello_project.domain.common.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponseDto {
    int statusCode;
    String message;

    public ErrorResponseDto(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public ErrorResponseDto(HttpStatus statusCode, String message) {
        this.statusCode = statusCode.value();
        this.message = message;
    }

    public ErrorResponseDto(HttpStatus statusCode) {
        this.statusCode = statusCode.value();
        this.message = "";
    }

    public ErrorResponseDto(int statusCode) {
        this.statusCode = statusCode;
        this.message = "";
    }
}
