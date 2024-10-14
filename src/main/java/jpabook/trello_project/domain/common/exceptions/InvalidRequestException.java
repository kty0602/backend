package jpabook.trello_project.domain.common.exceptions;

public class InvalidRequestException extends IllegalArgumentException{
    public InvalidRequestException(String message) {
        super(message);
    }
}
