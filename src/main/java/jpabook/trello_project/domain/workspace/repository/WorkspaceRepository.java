package jpabook.trello_project.domain.workspace.repository;

import jpabook.trello_project.domain.workspace.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
}
