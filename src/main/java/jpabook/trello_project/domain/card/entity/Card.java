package jpabook.trello_project.domain.card.entity;

import jakarta.persistence.*;
import jpabook.trello_project.domain.attachment.entity.Attachment;
import jpabook.trello_project.domain.card.dto.request.CreateCardRequestDto;
import jpabook.trello_project.domain.lists.entity.Lists;
import jpabook.trello_project.domain.manager.entity.Manager;
import jpabook.trello_project.domain.reply.entity.Reply;
import lombok.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "card")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Long id;
    @Column(length = 100)
    private String title;
    @Column(length = 100)
    private String info;
    @Column(length = 50)
    private LocalTime due;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_id", nullable = false)
    private Lists list;
    @OneToMany(mappedBy = "card", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Attachment> attachmentList = new ArrayList<>();
    @OneToMany(mappedBy = "card", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Reply> replyList = new ArrayList<>();
    @OneToMany(mappedBy = "card", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Manager> managers = new ArrayList<>();

    // lists 집어넣는거 아직 추가 안됨
    public Card(CreateCardRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.info = requestDto.getInfo();
        this.due = requestDto.getDue();
        // this.list = list;
        this.attachmentList = new ArrayList<>();
        this.replyList = new ArrayList<>();
        this.managers = new ArrayList<>();
    }

    public void changeTitle(String title) { this.title = title; }
    public void changeInfo(String info) { this.info = info; }
    public void changeDue(LocalTime due) { this.due = due; }

}
