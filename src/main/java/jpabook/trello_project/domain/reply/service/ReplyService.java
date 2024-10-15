package jpabook.trello_project.domain.reply.service;

import jpabook.trello_project.domain.card.dto.response.CardResponseDto;
import jpabook.trello_project.domain.card.entity.Card;
import jpabook.trello_project.domain.card.repository.CardRepository;
import jpabook.trello_project.domain.card.service.CardService;
import jpabook.trello_project.domain.common.dto.AuthUser;
import jpabook.trello_project.domain.common.exceptions.InvalidRequestException;
import jpabook.trello_project.domain.reply.dto.request.CreateReplyRequestDto;
import jpabook.trello_project.domain.reply.dto.request.ModifyReplyRequestDto;
import jpabook.trello_project.domain.reply.dto.response.ReplyResponseDto;
import jpabook.trello_project.domain.reply.entity.Reply;
import jpabook.trello_project.domain.reply.repository.ReplyRepository;
import jpabook.trello_project.domain.user.entity.User;
import jpabook.trello_project.domain.user.repository.UserRepository;
import jpabook.trello_project.domain.workspace_member.entity.WorkspaceMember;
import jpabook.trello_project.domain.workspace_member.enums.WorkRole;
import jpabook.trello_project.domain.workspace_member.repository.WorkspaceMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReplyService {
    private final ReplyRepository replyRepository;
    private final CardRepository cardRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReplyResponseDto createReply(CreateReplyRequestDto requestDto, Long cardId, AuthUser authUser) {
        log.info("::: 댓글 저장 로직 동작 :::");
        // 회원 정보 리턴
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new InvalidRequestException("해당 회원이 없습니다!"));
        // 카드 존재 유무 검사
        Card card = checkCardExist(cardId);
        // 멤버 로직 검사
        WorkspaceMember workspaceMember = checkMemberExist(authUser.getId());
        // 멤버 권한 검사
        validateWorkRole(workspaceMember.getWorkRole());

        Reply reply = new Reply(requestDto, card, user);
        Reply createReply = replyRepository.save(reply);
        log.info("::: 댓글 저장 로직 완료 :::");
        return new ReplyResponseDto(createReply);
    }

    @Transactional
    public ReplyResponseDto modifyReply(Long id, ModifyReplyRequestDto requestDto, AuthUser authUser) {
        log.info("::: 댓글 수정 로직 동작 :::");

        Reply reply = checkReplyExist(id);
        // 멤버 로직 검사
        WorkspaceMember workspaceMember = checkMemberExist(authUser.getId());
        // 멤버 권한 검사
        validateWorkRole(workspaceMember.getWorkRole());

        if(reply.getUser().getId() != authUser.getId()) {
            throw new InvalidRequestException("작성자만 댓글을 수정할 수 있습니다.");
        }

        if(requestDto.getContent() != null) {
            reply.changeContent(requestDto.getContent());
        }
        Reply newReply = replyRepository.save(reply);
        log.info("::: 댓글 수정 로직 완료 :::");
        return new ReplyResponseDto(newReply);
    }

    @Transactional
    public void deleteReply(Long id, AuthUser authUser) {
        log.info("::: 댓글 삭제 로직 동작 :::");

        Reply reply = checkReplyExist(id);
        // 멤버 로직 검사
        WorkspaceMember workspaceMember = checkMemberExist(authUser.getId());
        // 멤버 권한 검사
        validateWorkRole(workspaceMember.getWorkRole());

        if(reply.getUser().getId() != authUser.getId()) {
            throw new InvalidRequestException("작성자만 댓글을 삭제할 수 있습니다.");
        }

        replyRepository.deleteById(reply.getId());
        log.info("::: 댓글 삭제 로직 완료 :::");
    }


    private Card checkCardExist(Long cardId) {
        log.info("::: 카드 존재 여부 검사 로직 동작 :::");
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new InvalidRequestException("해당 카드가 없습니다!"));
    }

    private Reply checkReplyExist(Long id) {
        log.info("::: 댓글 존재 여부 검사 로직 동작 :::");
        return replyRepository.findById(id)
                .orElseThrow(() -> new InvalidRequestException("해당 댓글이 없습니다!"));
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
