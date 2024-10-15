package jpabook.trello_project.domain.card.dto.request;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCardRequestDto {
    private String title;
    private String info;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate due;

}
