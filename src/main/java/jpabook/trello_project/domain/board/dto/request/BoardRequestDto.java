package jpabook.trello_project.domain.board.dto.request;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class BoardRequestDto {
    private String title;
    private String backgroundColor;
}
