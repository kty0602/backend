package jpabook.trello_project.domain.common.exceptions;

import jpabook.trello_project.domain.common.dto.ErrorResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorStatus{
    _NOT_FOUND_USER(HttpStatus.NOT_FOUND, "가입되지 않은 유저입니다."),
    _DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "중복된 이메일입니다."),
    _BAD_REQUEST_PASSWORD(HttpStatus.BAD_REQUEST,"잘못된 비밀번호입니다."),;

    private HttpStatus httpStatus;
    private String message;

    @Override
    public ErrorResponseDto getErrorStatus() {
        return new ErrorResponseDto(httpStatus, message);
    }

}
