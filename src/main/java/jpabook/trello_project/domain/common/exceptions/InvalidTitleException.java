package jpabook.trello_project.domain.common.exceptions;

public class InvalidTitleException extends RuntimeException {
    public InvalidTitleException(String message) {
        super(message);
    }
}
