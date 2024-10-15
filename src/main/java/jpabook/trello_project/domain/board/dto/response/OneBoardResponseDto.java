package jpabook.trello_project.domain.board.dto.response;

import jpabook.trello_project.domain.board.entity.Board;
import jpabook.trello_project.domain.lists.dto.ListCardResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class OneBoardResponseDto {
    private Long boardId;
    private String title;
    private String backgroundColor;
    private String imgUrl;
    private List<ListCardResponseDto> listList;


    public OneBoardResponseDto(Board board, List<ListCardResponseDto> listList) {
        this.boardId = board.getId();
        this.title = board.getTitle();
        this.backgroundColor = board.getBackground_color();
        this.imgUrl = board.getImgUrl();
        this.listList = listList;
    }

}
