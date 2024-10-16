package jpabook.trello_project.domain.card_views.entity;

import jakarta.persistence.*;
import jpabook.trello_project.domain.card.entity.Card;
import jpabook.trello_project.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class CardViews {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_views_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id")
    private Card card;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public CardViews(User user, Card card) {
        this.user = user;
        this.card = card;
    }
}
