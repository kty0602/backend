package jpabook.trello_project.domain.board.dto.request;

import lombok.Getter;

@Getter
public class BoardRequestDto {
    private String title;
    private String backgroundColor;
    private String imgUrl;
}
