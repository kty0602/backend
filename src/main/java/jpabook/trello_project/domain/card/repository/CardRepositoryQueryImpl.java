package jpabook.trello_project.domain.card.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.trello_project.domain.card.dto.CardSearchCondition;
import jpabook.trello_project.domain.card.entity.Card;
import jpabook.trello_project.domain.card.entity.QCard;
import jpabook.trello_project.domain.manager.entity.QManager;
import jpabook.trello_project.domain.user.entity.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
public class CardRepositoryQueryImpl implements CardRepositoryQuery {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Card> findCardsByCondition(CardSearchCondition condition, Pageable pageable) {
        QCard card = QCard.card;
        QManager manager = QManager.manager;
        QUser user = QUser.user;

        BooleanBuilder builder = new BooleanBuilder();

        if (condition.getTitle() != null) {
            builder.and(card.title.eq(condition.getTitle()));
        }

        if (condition.getInfo() != null) {
            builder.and(card.info.eq(condition.getInfo()));
        }

        if (condition.getBoardId() != null) {
            builder.and(card.list.board.id.eq(condition.getBoardId()));
        }

        if (condition.getDue() != null) {
            LocalDate dueDate = condition.getDue();
            String formattedDate = dueDate.format(DateTimeFormatter.ISO_DATE); // "yyyy-MM-dd" 형식
            builder.and(card.due.stringValue().eq(formattedDate));
        }

        if (condition.getName() != null) {
            builder.and(card.managers.any().user.name.eq(condition.getName()));
        }

        List<Card> results = queryFactory
                .selectFrom(card)
                .leftJoin(card.managers, manager).fetchJoin()
                .leftJoin(manager.user, user).fetchJoin()
                .where(builder)
                .orderBy(card.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(card)
                .where(builder)
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }
}

