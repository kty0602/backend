package jpabook.trello_project.domain.workspace_member.entity;

import jakarta.persistence.*;
import jpabook.trello_project.domain.user.entity.User;
import jpabook.trello_project.domain.workspace.entity.Workspace;
import jpabook.trello_project.domain.workspace_member.enums.WorkRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.parameters.P;

@Getter
@Entity
@NoArgsConstructor
@Table(name="workspace_member")
public class WorkspaceMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="work_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_id", nullable = false)
    private Workspace workspace;

    @Enumerated(EnumType.STRING)
    private WorkRole workRole;

    public WorkspaceMember(User user, Workspace workspace, WorkRole workRole) {
        this.user = user;
        this.workspace = workspace;
        this.workRole = workRole;
    }

}
