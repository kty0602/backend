package jpabook.trello_project.domain.card.service;

import jpabook.trello_project.domain.card.dto.request.CreateCardRequestDto;
import jpabook.trello_project.domain.card.dto.response.CardResponseDto;
import jpabook.trello_project.domain.card.dto.response.GetCardResponseDto;
import jpabook.trello_project.domain.card.dto.request.ModifyCardRequestDto;
import jpabook.trello_project.domain.card.entity.Card;
import jpabook.trello_project.domain.card.repository.CardRepository;
import jpabook.trello_project.domain.common.dto.AuthUser;
import jpabook.trello_project.domain.common.exceptions.InvalidRequestException;
import jpabook.trello_project.domain.lists.entity.Lists;
import jpabook.trello_project.domain.lists.repository.ListRepository;
import jpabook.trello_project.domain.workspace_member.entity.WorkspaceMember;
import jpabook.trello_project.domain.workspace_member.enums.WorkRole;
import jpabook.trello_project.domain.workspace_member.repository.WorkspaceMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardService {
    private final CardRepository cardRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final ListRepository listRepository;

    @Transactional
    public CardResponseDto createCard(CreateCardRequestDto requestDto, Long listId, AuthUser authUser) {
        log.info("::: 카드 저장 로직 동작 :::");
        // 멤버 로직 검사
        WorkspaceMember workspaceMember = checkMemberExist(authUser.getId());
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
    public CardResponseDto modifyCard(Long id, ModifyCardRequestDto requestDto, AuthUser authUser) {
        log.info("::: 카드 수정 로직 동작 :::");

        // 카드 존재 유무 검사
        Card card = checkCardExist(id);
        // 멤버 로직 검사
        WorkspaceMember workspaceMember = checkMemberExist(authUser.getId());
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

    public GetCardResponseDto getCard(Long id, AuthUser authUser) {
        log.info("::: 카드 수정 조회 동작 :::");
        // 멤버 존재 검사 -> 이후 권한 검사 불필요
        checkMemberExist(authUser.getId());
        // 카드 존재 유무 검사
        Card card = checkCardExist(id);

        log.info("::: 카드 수정 조회 완료 :::");
        return new GetCardResponseDto(card);
    }

    @Transactional
    public void deleteCard(Long id, AuthUser authUser) {
        log.info("::: 카드 삭제 로직 동작 :::");

        Card card = checkCardExist(id);
        // 멤버 존재 검사
        WorkspaceMember workspaceMember = checkMemberExist(authUser.getId());
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

    private WorkspaceMember checkMemberExist(Long id) {
        log.info("::: 회원 -> 멤버 검사 로직 동작 :::");
        return workspaceMemberRepository.findById(id)
                .orElseThrow(() -> new InvalidRequestException("해당 멤버가 없습니다!"));
    }

    private void validateWorkRole(WorkRole workRole) {
        if (workRole == WorkRole.ROLE_READONLY) {
            throw new InvalidRequestException("읽기 전용 권한으로 해당 작업을 수행할 수 없습니다.");
        }
    }

}
