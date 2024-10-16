package jpabook.trello_project.domain.card.repository;

import jpabook.trello_project.domain.card.entity.Card;
import jpabook.trello_project.domain.lists.entity.Lists;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface CardRepository extends JpaRepository<Card, Long>, CardRepositoryQuery {
  List<Card> findByList(Lists list);

  @Modifying
  @Query(
  value = "UPDATE card c, " +
          "(SELECT card_id, RANK() OVER(ORDER BY view_count DESC) AS rnk FROM card LIMIT 5) t " +
          "SET c.rnk = t.rnk WHERE c.card_id = t.card_id",
  nativeQuery = true
  )
  void updateRankings();

  @Modifying
  @Query(
          value = "update card set rnk = 6",
          nativeQuery = true
  )
  void initRankings();

    List<Card> findTop5ByOrderByViewCountDesc();

  List<Card> findTop5ByList_Board_Workspace_IdAndList_Board_IdAndList_IdOrderByViewCountDesc(Long workspaceId, Long boardId, Long listId);
}
