package jpabook.trello_project.domain.board.entity;

import jakarta.persistence.*;
import jpabook.trello_project.domain.board.dto.request.BoardRequestDto;
import jpabook.trello_project.domain.workspace.entity.Workspace;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name="board")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="board_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_id", nullable = false)
    private Workspace workspace;

    private String title;
    private String background_color;
    private String imgUrl;

    // 기존 생성자
    public Board(Workspace workspace, String title, String background_color, String imgUrl) {
        this.workspace = workspace;
        this.title = title;
        this.background_color = background_color;
        this.imgUrl = imgUrl;
    }

    // 이미지 URL이 포함된 업데이트 메서드
    public Board update(BoardRequestDto boardRequestDto, String newImgUrl)  {
        // 제목 업데이트
        if(boardRequestDto.getTitle() != null) {
            this.title = boardRequestDto.getTitle();
        }

        // 배경색 업데이트
        if(boardRequestDto.getBackgroundColor() != null) {
            this.background_color = boardRequestDto.getBackgroundColor();
        }

        // 새로운 이미지 URL 업데이트
        if(newImgUrl != null && !newImgUrl.isEmpty()) {
            this.imgUrl = newImgUrl;  // S3에서 받은 새로운 이미지 URL 반영
        }

        return this;
    }

}

