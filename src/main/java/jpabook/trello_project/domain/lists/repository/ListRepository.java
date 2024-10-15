package jpabook.trello_project.domain.lists.repository;

import jpabook.trello_project.domain.board.entity.Board;
import jpabook.trello_project.domain.lists.entity.Lists;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ListRepository extends JpaRepository<Lists, Long> {

    @Query("SELECT COALESCE(MAX(l.listOrder), 0) FROM Lists l " +
            "WHERE l.board.id = :boardId")
    int findMaxListOrder(@Param("boardId") long boardId);

    @Modifying
    @Query("UPDATE Lists l SET l.listOrder = l.listOrder + 1 " +
            "WHERE l.listOrder >= :newOrder AND l.listOrder < :curOrder AND l.board.id = :boardId")
    void increaseListOrderBetween(@Param("newOrder") long newOrder, @Param("curOrder") long curOrder, @Param("boardId") long boardId);

    @Modifying
    @Query("UPDATE Lists l SET l.listOrder = l.listOrder - 1 " +
            "WHERE l.listOrder <= :newOrder AND l.listOrder > :curOrder AND l.board.id = :boardId")
    void decreaseListOrderBetween(@Param("newOrder") long newOrder, @Param("curOrder") long curOrder, @Param("boardId") long boardId);

    Optional<Lists> findByIdAndBoardId(Long listId, Long boardId);
    List<Lists> findAllByBoardId(Long boardId);
    List<Lists> findByBoard(Board board);
}
