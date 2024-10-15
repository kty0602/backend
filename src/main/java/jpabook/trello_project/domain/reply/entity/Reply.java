package jpabook.trello_project.domain.reply.entity;

import jakarta.persistence.*;

import jpabook.trello_project.domain.card.entity.Card;
import jpabook.trello_project.domain.reply.dto.request.CreateReplyRequestDto;
import jpabook.trello_project.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "reply")
public class Reply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_id")
    private Long id;

    @Column(length = 200)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 유저 추가해야함
    public Reply(CreateReplyRequestDto requestDto, Card card) {
        this.content = requestDto.getContent();
        this.card = card;
    }

    public void changeContent(String content) { this.content = content; }
}
