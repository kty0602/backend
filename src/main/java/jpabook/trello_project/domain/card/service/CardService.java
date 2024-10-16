package jpabook.trello_project.domain.card.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.trello_project.domain.board.entity.Board;
import jpabook.trello_project.domain.board.repository.BoardRepository;
import jpabook.trello_project.domain.card.dto.CardSearchCondition;
import jpabook.trello_project.domain.card.dto.request.CreateCardRequestDto;
import jpabook.trello_project.domain.card.dto.request.ModifyCardRequestDto;
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

    private static final String CARD_VIEW_COUNT_PREFIX = "card:viewcount:";
    private static final String CARD_USER_VIEW_PREFIX = "card:user:view:";
    private static final String CARD_TOP_RANKINGS = "card:toprankings";


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
    public GetCardResponseDto getCard(Long cardId, Long workId, AuthUser authUser) {
        User user = userService.findByEmail(authUser.getEmail());
        checkMemberExist(authUser.getId(), workId);

        Card card = checkCardExist(cardId);

        // 어뷰징 방지: 동일 사용자가 일정 시간 내에 다시 조회할 때는 조회수 증가시키지 않음
        String userCardViewKey = CARD_USER_VIEW_PREFIX + user.getId() + ":" + cardId;
        if (Boolean.FALSE.equals(redisTemplate.hasKey(userCardViewKey))) {
            // 조회수 증가 로직
            incrementViewCount(cardId);
            // 조회 기록 저장 (5초 동안 조회수 증가 방지)
            redisTemplate.opsForValue().set(userCardViewKey, true, Duration.ofSeconds(5));
        }

        log.info("카드 조회 완료");
        return new GetCardResponseDto(card);
    }

    private void incrementViewCount(Long cardId) {
        String redisKey = CARD_VIEW_COUNT_PREFIX + cardId;

        // Redis에서 조회수를 읽을 때 기본값 0L을 사용하고 명시적으로 Long으로 변환
        Long currentViewCount = redisTemplate.opsForValue().get(redisKey) != null ?
                Long.valueOf(redisTemplate.opsForValue().get(redisKey).toString()) : 0L;

        // Redis에 조회수 1 증가
        redisTemplate.opsForValue().set(redisKey, currentViewCount + 1);
    }

    private void updateTopRankings(Long cardId) {
        String redisKey = CARD_VIEW_COUNT_PREFIX + cardId;
        Long viewCount = (Long) redisTemplate.opsForValue().get(redisKey);

        // Redis에 저장된 상위 카드 목록을 가져옴
        List<Long> topRankings = (List<Long>) redisTemplate.opsForValue().get(CARD_TOP_RANKINGS);
        if (topRankings == null) {
            topRankings = List.of();
        }

        // 상위 5위 카드에 포함되어 있지 않으면 추가
        if (!topRankings.contains(cardId) && topRankings.size() < 5) {
            topRankings.add(cardId);
        }

        // 조회수로 정렬 후 상위 5위만 유지
        topRankings = topRankings.stream()
                .sorted((c1, c2) -> Long.compare(
                        (Long) redisTemplate.opsForValue().get(CARD_VIEW_COUNT_PREFIX + c2),
                        (Long) redisTemplate.opsForValue().get(CARD_VIEW_COUNT_PREFIX + c1)
                ))
                .limit(5)
                .collect(Collectors.toList());

        redisTemplate.opsForValue().set(CARD_TOP_RANKINGS, topRankings);
    }

    public List<Long> getTopRankings() {
        return (List<Long>) redisTemplate.opsForValue().get(CARD_TOP_RANKINGS);
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
