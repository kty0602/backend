package jpabook.trello_project.domain.workspace_member;

import jpabook.trello_project.domain.workspace_member.entity.WorkspaceMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, Long> {

    Optional<WorkspaceMember> findByUserIdAndWorkspaceId(Long userId, Long workId);
}
