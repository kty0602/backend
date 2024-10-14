package jpabook.trello_project.domain.lists.entity;

import jakarta.persistence.*;
import jpabook.trello_project.domain.board.entity.Board;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "list")
public class Lists {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lists_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    private String title;
    @Column(name = "list_order")
    private long order;

    public Lists(Board board, String title, long order) {
        this.board = board;
        this.title = title;
        this.order = order;
    }
}
