package jpabook.trello_project.domain.common.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiException extends RuntimeException {

    private final BaseErrorStatus errorStatus;
}
