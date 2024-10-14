package jpabook.trello_project.domain.card.entity;

import jakarta.persistence.*;
import jpabook.trello_project.domain.lists.entity.Lists;
import lombok.*;

import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "card")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Long id;
    @Column(length = 100)
    private String title;
    @Column(length = 100)
    private String info;
    private LocalTime due;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_id", nullable = false)
    private Lists list;

}
