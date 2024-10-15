package jpabook.trello_project.domain.board.repository;

import jpabook.trello_project.domain.board.entity.Board;
import jpabook.trello_project.domain.workspace.entity.Workspace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Optional<Board> findByIdAndWorkspace(Long boardId, Workspace workspace);

    Page<Board> findByWorkspace(Workspace workspace, Pageable pageable);
}
