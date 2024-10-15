package jpabook.trello_project.domain.workspace;

import jpabook.trello_project.domain.workspace.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkSpaceRepository extends JpaRepository<Workspace, Long> {
}
