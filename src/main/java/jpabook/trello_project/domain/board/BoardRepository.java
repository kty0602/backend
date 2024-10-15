package jpabook.trello_project.domain.board;

import jpabook.trello_project.domain.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
