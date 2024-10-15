package jpabook.trello_project.domain.workspace.repository;

import jpabook.trello_project.domain.common.dto.AuthUser;
import jpabook.trello_project.domain.workspace.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {

    Optional<Workspace> findById(Long id);

    Optional<Workspace> findByUser(AuthUser authUser);
}
