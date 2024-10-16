package jpabook.trello_project.domain.card.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.trello_project.domain.board.entity.Board;
import jpabook.trello_project.domain.board.repository.BoardRepository;
import jpabook.trello_project.domain.card.dto.CardSearchCondition;
import jpabook.trello_project.domain.card.dto.request.CreateCardRequestDto;
import jpabook.trello_project.domain.card.dto.request.ModifyCardRequestDto;
import jpabook.trello_project.domain.card.dto.response.CardRankResponseDto;
import jpabook.trello_project.domain.card.dto.response.CardResponseDto;
import jpabook.trello_project.domain.card.dto.response.CardSearchResponse;
import jpabook.trello_project.domain.card.dto.response.GetCardResponseDto;
import jpabook.trello_project.domain.card.entity.Card;
import jpabook.trello_project.domain.card.repository.CardRepository;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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


    private final RedisTemplate<String, Object> redisTemplate;

    public static final String CARD_VIEW_COUNT_PREFIX = "card:viewcount:";
    private static final String CARD_USER_VIEW_PREFIX = "card:user:view:";
    private static final String CARD_TOP_RANKINGS = "card:toprankings";


    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public CardResponseDto createCard(CreateCardRequestDto requestDto, Long workId, Long listId, AuthUser authUser) {
        // 멤버 로직 검사
        WorkspaceMember workspaceMember = checkMemberExist(authUser.getId(), workId);
        // 멤버 권한 검사
        validateWorkRole(workspaceMember.getWorkRole());

        Lists list = listRepository.findById(listId).orElseThrow(() -> new InvalidRequestException("해당 리스트가 없습니다!"));

        Card card = new Card(requestDto, list);
        Card createCard = cardRepository.save(card);
        return new CardResponseDto(createCard);
    }

    @Transactional
    public CardResponseDto modifyCard(Long id, ModifyCardRequestDto requestDto, Long workId, AuthUser authUser) {

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
    public GetCardResponseDto getCard(Long cardId, Long workId, Long boardId, Long listId, AuthUser authUser) {
        User user = userService.findByEmail(authUser.getEmail());
        checkMemberExist(authUser.getId(), workId);

        Card card = checkCardExist(cardId);

        Long viewCount;
        String userCardViewKey = CARD_USER_VIEW_PREFIX + user.getId() + ":" + workId + ":" + boardId + ":" + listId + ":" + cardId;
        if (Boolean.FALSE.equals(redisTemplate.hasKey(userCardViewKey))) {
            // 조회수 증가
            viewCount = incrementViewCount(card, workId, boardId, listId);
            // 5초 동안 중복 조회 방지
            redisTemplate.opsForValue().set(userCardViewKey, true, Duration.ofSeconds(5));
            // 상위 랭킹 업데이트
            updateTopRankings(card, workId, boardId, listId);
        } else {
            viewCount = getViewCount(card, workId, boardId, listId);
        }

        return new GetCardResponseDto(card, viewCount);
    }

    private Long incrementViewCount(Card card, Long workId, Long boardId, Long listId) {
        String redisKey = CARD_VIEW_COUNT_PREFIX + workId + ":" + boardId + ":" + listId + ":" + card.getId();
        Long currentViewCount = getViewCount(card, workId, boardId, listId);
        // Redis에 조회수 1 증가
        redisTemplate.opsForValue().set(redisKey, currentViewCount + 1);
        return currentViewCount + 1;
    }

    private Long getViewCount(Card card, Long workId, Long boardId, Long listId) {
        String redisKey = CARD_VIEW_COUNT_PREFIX + workId + ":" + boardId + ":" + listId + ":" + card.getId();

        // Redis에서 조회 후 miss일 경우 DB에서 조회
        Long currentViewCount;
        Object cache = redisTemplate.opsForValue().get(redisKey);
        if (cache == null) {
            currentViewCount = card.getViewCount();
        } else {
            currentViewCount = Long.parseLong(cache.toString());
        }
        return currentViewCount;
    }

    private void updateTopRankings(Card card, Long workId, Long boardId, Long listId) {
        String rankingKey = CARD_TOP_RANKINGS + workId + ":" + boardId + ":" + listId;
        Long viewCount = getViewCount(card, workId, boardId, listId);

        // Redis에서 해당 workId, boardId, listId에 맞는 상위 5개의 카드 랭킹 가져오기
        Object cache = redisTemplate.opsForValue().get(rankingKey);
        List<Long> topRankings;
        if (cache instanceof List<?>) {
            topRankings = (List<Long>) cache;
        } else {
            topRankings = new ArrayList<>();
        }

        // 현재 카드를 상위 랭킹 목록에서 제거하고 다시 추가
        topRankings.remove(card.getId());
        topRankings.add(card.getId());

        // 조회수에 따라 정렬 후 상위 5개만 유지
        topRankings = topRankings.stream()
                .sorted((c1, c2) -> Long.compare(getViewCount(cardRepository.findById(c2).orElseThrow(),
                                workId, boardId, listId),
                        getViewCount(cardRepository.findById(c1).orElseThrow(),
                                workId, boardId, listId)))
                .limit(5)
                .collect(Collectors.toList());

        // Redis에 상위 랭킹 업데이트
        redisTemplate.opsForValue().set(rankingKey, topRankings);
    }


    public List<CardRankResponseDto> getTop5Cards(Long workId, Long boardId, Long listId) {
        // Redis에서 상위 랭킹 조회
        String rankingKey = CARD_TOP_RANKINGS + workId + ":" + boardId + ":" + listId;
        List<Long> topRankings = (List<Long>) redisTemplate.opsForValue().get(rankingKey);
        if (topRankings == null || topRankings.isEmpty()) {
            throw new InvalidRequestException("상위 카드가 없습니다.");
        }

        // 카드 정보를 순서대로 조회하여 DTO로 반환
        List<CardRankResponseDto> topCardDtos = new ArrayList<>();
        for (Long cardId : topRankings) {
            Card card = cardRepository.findById(cardId)
                    .orElseThrow(() -> new InvalidRequestException("해당 카드가 존재하지 않습니다. 카드 ID: " + cardId));
            topCardDtos.add(new CardRankResponseDto(card));
        }

        return topCardDtos;
    }

    @Transactional
    public void deleteCard(Long id, Long workId, AuthUser authUser) {

        Card card = checkCardExist(id);
        // 멤버 존재 검사
        WorkspaceMember workspaceMember = checkMemberExist(authUser.getId(), workId);
        // 멤버 권한 검사
        validateWorkRole(workspaceMember.getWorkRole());

        cardRepository.deleteById(card.getId());
    }


    private Card checkCardExist(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new InvalidRequestException("해당 카드가 없습니다!"));
    }

    private WorkspaceMember checkMemberExist(Long id, Long workId) {
        return workspaceMemberRepository.findByUserIdAndWorkspaceId(id, workId)
                .orElseThrow(() -> new InvalidRequestException("해당 멤버가 없습니다!"));
    }

    private void validateWorkRole(WorkRole workRole) {
        if (workRole == WorkRole.ROLE_READONLY) {
            throw new InvalidRequestException("읽기 전용 권한으로 해당 작업을 수행할 수 없습니다.");
        }
    }

}
