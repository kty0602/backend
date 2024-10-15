package jpabook.trello_project.domain.manager.entity;

import jakarta.persistence.*;
import jpabook.trello_project.domain.card.entity.Card;
import jpabook.trello_project.domain.manager.dto.request.ManagerRequestDto;
import jpabook.trello_project.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "manager")
public class Manager {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_user_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Manager (Card card, User user) {
        this.card = card;
        this.user = user;
    }
}
