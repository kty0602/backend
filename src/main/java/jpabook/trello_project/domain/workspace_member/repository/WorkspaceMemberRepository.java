package jpabook.trello_project.domain.workspace_member.repository;

import jpabook.trello_project.domain.user.entity.User;
import jpabook.trello_project.domain.workspace_member.entity.WorkspaceMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, Long> {
    Optional<WorkspaceMember> findByUser(User user);
    boolean existsByUser(User user);
}
