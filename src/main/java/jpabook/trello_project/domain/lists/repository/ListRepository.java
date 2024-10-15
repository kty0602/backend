package jpabook.trello_project.domain.lists.repository;

import jpabook.trello_project.domain.lists.entity.Lists;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ListRepository extends JpaRepository<Lists, Long> {

    @Query("SELECT COALESCE(MAX(l.listOrder), 0) FROM Lists l ")
    int findMaxListOrder();

    @Modifying
    @Query("UPDATE Lists l SET l.listOrder = l.listOrder + 1 " +
            "WHERE l.listOrder >= :newOrder AND l.listOrder < :curOrder")
    void increaseListOrderBetween(@Param("newOrder") long newOrder, @Param("curOrder") long curOrder);

    @Modifying
    @Query("UPDATE Lists l SET l.listOrder = l.listOrder - 1 " +
            "WHERE l.listOrder <= :newOrder AND l.listOrder > :curOrder")
    void decreaseListOrderBetween(@Param("newOrder") long newOrder, @Param("curOrder") long curOrder);

    Optional<Lists> findByIdAndBoardId(Long listId, Long boardId);

    List<Lists> findAllByBoardId(Long boardId);
}
