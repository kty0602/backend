package jpabook.trello_project.domain.board.dto.response;

import jpabook.trello_project.domain.board.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoardResponseDto {
    private Long boardId;
    private String title;
    private String backgroundColor;
    private String imgUrl;


    public BoardResponseDto(Board board) {
        this.boardId = board.getId();
        this.title = board.getTitle();
        this.backgroundColor = board.getBackground_color();
        this.imgUrl = board.getImgUrl();
    }
}
