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
    _BAD_REQUEST_USER_ROLE(HttpStatus.BAD_REQUEST,"유효하지 않은 UserRole 입니다."),
    _BAD_REQUEST_USER(HttpStatus.BAD_REQUEST,"탈퇴한 유저입니다."),
    _UNAUTHORIZED_SIGNIN(HttpStatus.UNAUTHORIZED,"로그인에 실패하였습니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"인증이 필요합니다."),;

    private HttpStatus httpStatus;
    private String message;

    @Override
    public ErrorResponseDto getErrorStatus() {
        return new ErrorResponseDto(httpStatus, message);
    }

}
