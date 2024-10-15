package jpabook.trello_project.domain.reply.dto.response;

import jpabook.trello_project.domain.reply.entity.Reply;
import lombok.Getter;

@Getter
public class ReplyResponseDto {
    private Long id;
    private String content;
    private Long userId;
    private Long cardId;

    public ReplyResponseDto(Reply reply) {
        this.id = reply.getId();
        this.content = reply.getContent();
        this.userId = reply.getUser().getId();
        this.cardId = reply.getCard().getId();
    }
}
