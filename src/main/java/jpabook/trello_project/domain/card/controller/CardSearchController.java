package jpabook.trello_project.domain.card.controller;

import jpabook.trello_project.domain.card.dto.CardSearchCondition;
import jpabook.trello_project.domain.card.dto.response.CardRankResponseDto;
import jpabook.trello_project.domain.card.dto.response.CardSearchResponse;
import jpabook.trello_project.domain.card.service.CardService;
import jpabook.trello_project.domain.common.dto.AuthUser;
import jpabook.trello_project.domain.common.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class CardSearchController {
    private final CardService cardService;

    @GetMapping("/workspaces/{workId}/boards/{boardId}/cards")
    public ResponseDto<Page<CardSearchResponse>> search(@AuthenticationPrincipal AuthUser authUser,
                                                        @PathVariable Long workId,
                                                        @PathVariable Long boardId,
                                                        @RequestParam(defaultValue = "1") int page,
                                                        @RequestParam(defaultValue = "10") int size,
                                                        @RequestParam(required = false) String title,
                                                        @RequestParam(required = false) String info,
                                                        @RequestParam(required = false) LocalDate due,
                                                        @RequestParam(required = false) String name
    ) {
        CardSearchCondition cardSearchCondition = new CardSearchCondition(title, info, due, name, boardId);
        return ResponseDto.of(HttpStatus.OK, cardService.searchCards(authUser, page, size, cardSearchCondition));
    }

    @GetMapping("/cards/top5")
    public ResponseEntity<ResponseDto<List<CardRankResponseDto>>> getTop5(
            @AuthenticationPrincipal AuthUser authUser) {
        List<CardRankResponseDto> responseDto = cardService.getTop5Cards();
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.of(200, responseDto));
    }
}
