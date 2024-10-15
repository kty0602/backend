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
    _BAD_REQUEST_PASSWORD(HttpStatus.BAD_REQUEST,"잘못된 비밀번호입니다."),
    _INVALID_WORK_ROLE(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    _NOT_FOUND_WORKSPACE(HttpStatus.NOT_FOUND, "요청한 워크스페이스를 찾을 수 없습니다."),
    _NOT_FOUND_BOARD(HttpStatus.NOT_FOUND, "요청한 보드를 찾을 수 없습니다."),
    _NOT_FOUND_LIST(HttpStatus.NOT_FOUND, "요청한 리스트를 찾을 수 없습니다.");

    private HttpStatus httpStatus;
    private String message;

    @Override
    public ErrorResponseDto getErrorStatus() {
        return new ErrorResponseDto(httpStatus, message);
    }

}
