package jpabook.trello_project.domain.card.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardSearchCondition {
    private String title;
    private String info;
    private LocalDate due;
    private String name;
    private Long boardId;
}
