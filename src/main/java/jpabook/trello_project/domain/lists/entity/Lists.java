package jpabook.trello_project.domain.lists.entity;

import jakarta.persistence.*;
import jpabook.trello_project.domain.board.entity.Board;
import jpabook.trello_project.domain.card.entity.Card;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Lists {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lists_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    private String title;

    private long listOrder;

    @OneToMany(mappedBy = "list", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Card> cards;

    public Lists(Board board, String title, long listOrder) {
        this.board = board;
        this.title = title;
        this.listOrder = listOrder;
    }

    public void changeTitle(String title) {
        this.title = title;
    }

    public void changeListOrder(long listOrder) {
        this.listOrder = listOrder;
    }
}
