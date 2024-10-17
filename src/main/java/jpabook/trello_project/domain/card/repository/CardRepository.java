package jpabook.trello_project.domain.card.repository;

import jpabook.trello_project.domain.card.dto.CardViewCount;
import jpabook.trello_project.domain.card.entity.Card;
import jpabook.trello_project.domain.lists.entity.Lists;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.JdbcTemplate;
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

    @Modifying
    @Query("UPDATE Card c SET c.viewCount = 0")
    void resetAllViewCounts();

    List<CardViewCount> findAllByOrderByViewCountDesc(Limit limit);
}
