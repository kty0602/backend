package jpabook.trello_project.domain.common.exceptions;

import jpabook.trello_project.domain.common.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;

public interface BaseErrorStatus {
    public ErrorResponseDto getErrorStatus();
    public HttpStatus getHttpStatus();
}
