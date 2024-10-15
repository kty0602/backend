package jpabook.trello_project.domain.manager.service;

import jpabook.trello_project.domain.card.entity.Card;
import jpabook.trello_project.domain.card.repository.CardRepository;
import jpabook.trello_project.domain.common.dto.AuthUser;
import jpabook.trello_project.domain.common.exceptions.InvalidRequestException;
import jpabook.trello_project.domain.manager.dto.request.ManagerRequestDto;
import jpabook.trello_project.domain.manager.dto.response.ManagerResponseDto;
import jpabook.trello_project.domain.manager.entity.Manager;
import jpabook.trello_project.domain.manager.repository.ManagerRepository;
import jpabook.trello_project.domain.workspace_member.entity.WorkspaceMember;
import jpabook.trello_project.domain.workspace_member.repository.WorkspaceMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ManagerService {
    private final ManagerRepository managerRepository;
    private final CardRepository cardRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    @Transactional
    public ManagerResponseDto createManager(ManagerRequestDto requestDto, Long cardId, AuthUser authUser) {
        log.info("::: 담당자 저장 로직 동작 :::");
        // 요청 보내는 사람이 워크스페이스 멤버인가?
        checkMemberExist(authUser.getId());
        // 담당자가 현재 워크스페이스 멤버인지 확인
        WorkspaceMember workspaceMember = checkMemberExist(requestDto.getUserId());
        // 배정하려고 하는 카드가 존재하는지
        Card card  = checkCardExist(cardId);

        Manager manager = new Manager(card, workspaceMember.getUser());
        Manager newManager = managerRepository.save(manager);
        log.info("::: 담당자 저장 동작 완료 :::");
        return new ManagerResponseDto(newManager);
    }

    private Card checkCardExist(Long cardId) {
        log.info("::: 카드 존재 여부 검사 로직 동작 :::");
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new InvalidRequestException("해당 카드가 없습니다!"));
    }

    private WorkspaceMember checkMemberExist(Long userId) {
        log.info("::: 멤버 검사 로직 동작 :::");
        return workspaceMemberRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("해당 멤버가 없습니다!"));
    }
}
