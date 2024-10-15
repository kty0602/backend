package jpabook.trello_project.domain.card.dto.response;

import jpabook.trello_project.domain.attachment.dto.response.AttachmentResponseDto;
import jpabook.trello_project.domain.card.entity.Card;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class GetCardResponseDto {
    private Long id;
    private String title;
    private String info;
    private LocalTime due;
    private List<AttachmentResponseDto> attachmentList;

    public GetCardResponseDto(Card card) {
        this.id = card.getId();
        this.title = card.getTitle();
        this.info = card.getInfo();
        this.due = card.getDue();
        this.attachmentList = card.getAttachmentList().stream()
                .map(AttachmentResponseDto::new)
                .collect(Collectors.toList());
    }

}
