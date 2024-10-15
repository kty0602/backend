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

    @Transactional
    public ReplyResponseDto createReply(CreateReplyRequestDto requestDto, Long cardId, AuthUser authUser) {
        log.info("::: 댓글 저장 로직 동작 :::");
        // 회원 로직 검사
        log.info("::: 회원 -> 멤버 검사 로직 동작 :::");

        log.info("::: 멤버 권한 로직 동작 :::");

        log.info("::: 카드 존재 확인 로직 동작 :::");
        Card card = checkCardExist(cardId);

        Reply reply = new Reply(requestDto, card);
        Reply createReply = replyRepository.save(reply);
        log.info("::: 댓글 저장 로직 완료 :::");
        return new ReplyResponseDto(createReply);
    }

    @Transactional
    public ReplyResponseDto modifyReply(Long id, ModifyReplyRequestDto requestDto, AuthUser authUser) {
        log.info("::: 댓글 수정 로직 동작 :::");

        Reply reply = checkReplyExist(id);

        log.info("::: 회원 -> 멤버 검사 로직 동작 :::");

        log.info("::: 멤버 권한 로직 동작 :::");


        if(requestDto.getContent() != null) {
            reply.changeContent(requestDto.getContent());
        }
        Reply newReply = replyRepository.save(reply);
        log.info("::: 댓글 수정 로직 완료 :::");
        return new ReplyResponseDto(newReply);
    }

    @Transactional
    public void deleteReply(Long id, AuthUser authUser) {
        log.info("::: 댓글 수정 로직 동작 :::");

        Reply reply = checkReplyExist(id);

        log.info("::: 회원 -> 멤버 검사 로직 동작 :::");

        log.info("::: 멤버 권한 로직 동작 :::");

        replyRepository.deleteById(id);
        log.info("::: 댓글 수정 로직 완료 :::");
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


}
