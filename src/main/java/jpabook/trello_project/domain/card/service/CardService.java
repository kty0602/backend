package jpabook.trello_project.domain.card.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.trello_project.domain.board.entity.Board;
import jpabook.trello_project.domain.board.repository.BoardRepository;
import jpabook.trello_project.domain.card.dto.CardSearchCondition;
import jpabook.trello_project.domain.card.dto.request.CreateCardRequestDto;
import jpabook.trello_project.domain.card.dto.response.CardResponseDto;
import jpabook.trello_project.domain.card.dto.response.CardSearchResponse;
import jpabook.trello_project.domain.card.dto.response.GetCardResponseDto;
import jpabook.trello_project.domain.card.dto.request.ModifyCardRequestDto;
import jpabook.trello_project.domain.card.entity.Card;
import jpabook.trello_project.domain.card.repository.CardRepository;
import jpabook.trello_project.domain.card_views.entity.CardViews;
import jpabook.trello_project.domain.card_views.service.CardViewsService;
import jpabook.trello_project.domain.common.dto.AuthUser;
import jpabook.trello_project.domain.common.exceptions.ApiException;
import jpabook.trello_project.domain.common.exceptions.ErrorStatus;
import jpabook.trello_project.domain.common.exceptions.InvalidRequestException;
import jpabook.trello_project.domain.lists.entity.Lists;
import jpabook.trello_project.domain.lists.repository.ListRepository;
import jpabook.trello_project.domain.user.entity.User;
import jpabook.trello_project.domain.user.service.UserService;
import jpabook.trello_project.domain.workspace_member.entity.WorkspaceMember;
import jpabook.trello_project.domain.workspace_member.enums.WorkRole;
import jpabook.trello_project.domain.workspace_member.repository.WorkspaceMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardService {
    private final CardRepository cardRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final ListRepository listRepository;
    private final BoardRepository boardRepository;
    private final UserService userService;
    private final CardViewsService cardViewsService;

    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public CardResponseDto createCard(CreateCardRequestDto requestDto, Long workId, Long listId, AuthUser authUser) {
        log.info("::: 카드 저장 로직 동작 :::");
        // 멤버 로직 검사
        WorkspaceMember workspaceMember = checkMemberExist(authUser.getId(), workId);
        // 멤버 권한 검사
        validateWorkRole(workspaceMember.getWorkRole());

        log.info("::: lists 반환 로직 동작 :::");
        Lists list = listRepository.findById(listId).orElseThrow(() -> new InvalidRequestException("해당 리스트가 없습니다!"));

        Card card = new Card(requestDto, list);
        Card createCard = cardRepository.save(card);
        log.info("::: 카드 저장 로직 완료 :::");
        return new CardResponseDto(createCard);
    }

    @Transactional
    public CardResponseDto modifyCard(Long id, ModifyCardRequestDto requestDto, Long workId, AuthUser authUser) {
        log.info("::: 카드 수정 로직 동작 :::");

        // 카드 존재 유무 검사
        Card card = checkCardExist(id);
        // 멤버 로직 검사
        WorkspaceMember workspaceMember = checkMemberExist(authUser.getId(), workId);
        // 멤버 권한 검사
        validateWorkRole(workspaceMember.getWorkRole());

        if(requestDto.getTitle() != null) {
            card.changeTitle(requestDto.getTitle());
        }
        if(requestDto.getInfo() != null) {
            card.changeInfo(requestDto.getInfo());
        }if(requestDto.getDue() != null) {
            card.changeDue(requestDto.getDue());
        }
        Card newCard = cardRepository.save(card);
        log.info("::: 카드 수정 로직 완료 :::");
        return new CardResponseDto(newCard);
    }

    public Page<CardSearchResponse> searchCards(AuthUser authUser, int page, int size, CardSearchCondition condition) {

        User user = userService.findByEmail(authUser.getEmail());

        // board id 유효성 검사
        Board board = boardRepository.findById(condition.getBoardId())
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_BOARD));

        // workspace 멤버인지 확인
        if (!workspaceMemberRepository.existsByUserAndWorkspace(user, board.getWorkspace()))
            throw new ApiException(ErrorStatus._UNAUTHORIZED_USER);

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Card> cards = cardRepository.findCardsByCondition(condition, pageable);

        return cards.map(card -> new CardSearchResponse(
                card.getId(),
                card.getList().getId(),
                card.getTitle(),
                card.getInfo(),
                card.getDue()
        ));
    }

    @Transactional
    public GetCardResponseDto getCard(Long id, Long workId, AuthUser authUser) {
        log.info("::: 카드 수정 조회 동작 :::");
        // 유저 정보 가져오기
        User user = userService.findByEmail(authUser.getEmail());

        // 멤버 존재 검사 -> 이후 권한 검사 불필요
        checkMemberExist(authUser.getId(), workId);
        // 카드 존재 유무 검사
        Card card = checkCardExist(id);

        // 어뷰징 방지 로직
        CardViews history = cardViewsService.findByUserAndCard(user, card);

        // 조회 기록이 없거나 마지막 조회 기록이 10분 이상 지난 경우
        if (history == null
                || Duration.between(history.getCreatedAt(), LocalDateTime.now()).getSeconds() > 600) {
            card.plusViewCount();
            updateRanking();
            entityManager.flush(); // 네이티브 쿼리 사용 후 엔티티 매니저 동기화
            cardViewsService.save(user, card);
        }

        log.info("::: 카드 수정 조회 완료 :::");
        return new GetCardResponseDto(card);
    }

    @Transactional
    public void deleteCard(Long id, Long workId, AuthUser authUser) {
        log.info("::: 카드 삭제 로직 동작 :::");

        Card card = checkCardExist(id);
        // 멤버 존재 검사
        WorkspaceMember workspaceMember = checkMemberExist(authUser.getId(), workId);
        // 멤버 권한 검사
        validateWorkRole(workspaceMember.getWorkRole());

        cardRepository.deleteById(card.getId());
        log.info("::: 카드 삭제 로직 완료 :::");
    }

    private void updateRanking() {
        // 상위 5위까지 저장
        cardRepository.initRankings();
        cardRepository.updateRankings();
    }

    private Card checkCardExist(Long id) {
        log.info("::: 카드 존재 여부 검사 로직 동작 :::");
        return cardRepository.findById(id)
                .orElseThrow(() -> new InvalidRequestException("해당 카드가 없습니다!"));
    }

    private WorkspaceMember checkMemberExist(Long id, Long workId) {
        log.info("::: 회원 -> 멤버 검사 로직 동작 :::");
        return workspaceMemberRepository.findByUserIdAndWorkspaceId(id, workId)
                .orElseThrow(() -> new InvalidRequestException("해당 멤버가 없습니다!"));
    }

    private void validateWorkRole(WorkRole workRole) {
        if (workRole == WorkRole.ROLE_READONLY) {
            throw new InvalidRequestException("읽기 전용 권한으로 해당 작업을 수행할 수 없습니다.");
        }
    }

}
