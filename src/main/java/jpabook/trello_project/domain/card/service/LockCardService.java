package jpabook.trello_project.domain.card.service;

import jpabook.trello_project.domain.card.dto.request.ModifyCardRequestDto;
import jpabook.trello_project.domain.card.dto.response.CardResponseDto;
import jpabook.trello_project.domain.card.entity.Card;
import jpabook.trello_project.domain.card.repository.CardRepository;
import jpabook.trello_project.domain.common.dto.AuthUser;
import jpabook.trello_project.domain.common.exceptions.InvalidRequestException;
import jpabook.trello_project.domain.workspace_member.entity.WorkspaceMember;
import jpabook.trello_project.domain.workspace_member.enums.WorkRole;
import jpabook.trello_project.domain.workspace_member.repository.WorkspaceMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LockCardService {

    private final CardRepository cardRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    @Transactional
    public CardResponseDto modifyCardWithPessimisticLock(Long id, ModifyCardRequestDto requestDto, Long workId, AuthUser authUser) {
        log.info("::: 비관적 락을 사용한 카드 수정 :::");

        // 카드 존재 유무 검사
        Card card = cardRepository.findByIdWithPessimisticLock(id)
                .orElseThrow(() -> new InvalidRequestException("해당 카드가 없습니다!"));

        return getModifiedCardResponseDto(requestDto, workId, authUser, card);
    }

    @Transactional
    public CardResponseDto modifyCardWithOptimisticLock(Long id, ModifyCardRequestDto requestDto, Long workId, AuthUser authUser) throws InterruptedException {
        log.info("::: 낙관적 락을 사용한 카드 수정 :::");

        while(true) {
            try {
                // 카드 존재 유무 검사
                Card card = cardRepository.findByIdWithOptimisticLock(id)
                        .orElseThrow(() -> new InvalidRequestException("해당 카드가 없습니다!"));

                return getModifiedCardResponseDto(requestDto, workId, authUser, card);
            } catch (ObjectOptimisticLockingFailureException e) {
                Thread.sleep(50);
            }
        }

    }

    @NotNull
    private CardResponseDto getModifiedCardResponseDto(ModifyCardRequestDto requestDto, Long workId, AuthUser authUser, Card card) {
        // 멤버 로직 검사
        WorkspaceMember workspaceMember = checkMemberExist(authUser.getId(), workId);
        // 멤버 권한 검사
        validateWorkRole(workspaceMember.getWorkRole());

        if(requestDto.getTitle() != null) {
            card.changeTitle(requestDto.getTitle());
        }
        if(requestDto.getInfo() != null) {
            card.changeInfo(requestDto.getInfo());
        }
        if(requestDto.getDue() != null) {
            card.changeDue(requestDto.getDue());
        }
        Card newCard = cardRepository.saveAndFlush(card);
        log.info("::: 카드 수정 로직 완료 :::");
        return new CardResponseDto(newCard);
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
