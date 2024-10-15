package jpabook.trello_project.domain.card.controller;

import jpabook.trello_project.domain.card.dto.CardSearchCondition;
import jpabook.trello_project.domain.card.dto.response.CardSearchResponse;
import jpabook.trello_project.domain.card.service.CardService;
import jpabook.trello_project.domain.common.dto.AuthUser;
import jpabook.trello_project.domain.common.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards/{boardId}/cards")
public class CardSearchController {
    private final CardService cardService;

    @GetMapping
    public ResponseDto<Page<CardSearchResponse>> search(@AuthenticationPrincipal AuthUser authUser,
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
}
