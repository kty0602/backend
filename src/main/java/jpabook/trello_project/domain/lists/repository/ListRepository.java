package jpabook.trello_project.domain.lists.repository;

import jpabook.trello_project.domain.lists.entity.Lists;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ListRepository extends JpaRepository<Lists, Long> {

    @Query("SELECT COALESCE(MAX(l.listOrder), 0) FROM Lists l ")
    int findMaxListOrder();

    @Modifying
    @Query("UPDATE Lists l SET l.listOrder = l.listOrder + 1 WHERE l.listOrder < :changedOrder")
    void updateListOrders(@Param("changedOrder") long changedOrder);

}
