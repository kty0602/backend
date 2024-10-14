package jpabook.trello_project.domain.board.entity;

import jakarta.persistence.*;
import jpabook.trello_project.domain.workspace.entity.Workspace;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name="board")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="board_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_id", nullable = false)
    private Workspace workspace;

    private String title;
    private String background_color;
    private String imgUrl;

    public Board(Workspace workspace, String title, String background_color, String imgUrl) {
        this.workspace = workspace;
        this.title = title;
        this.background_color = background_color;
        this.imgUrl = imgUrl;
    }

}
