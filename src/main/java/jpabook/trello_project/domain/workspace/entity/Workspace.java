package jpabook.trello_project.domain.workspace.entity;

import jakarta.persistence.*;
import jpabook.trello_project.domain.board.entity.Board;
import jpabook.trello_project.domain.user.entity.User;
import jpabook.trello_project.domain.workspace.dto.request.WorkspaceRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@Table(name="workspace")
public class Workspace {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="work_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "workspace", cascade = CascadeType.REMOVE)
    private List<Board> boards = new ArrayList<>();

    private String title;
    private String info;

    public Workspace(User user, String title, String info) {
        this.user = user;
        this.title = title;
        this.info = info;
    }

    public Workspace update(WorkspaceRequestDto workspaceRequestDto) {
        if (workspaceRequestDto.getTitle() != null) {
            this.title = workspaceRequestDto.getTitle();
        }

        if (workspaceRequestDto.getInfo() != null) {
            this.info = workspaceRequestDto.getInfo();
        }

        return this;
    }
}
