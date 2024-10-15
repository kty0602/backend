package jpabook.trello_project.domain.card.service;

import jpabook.trello_project.domain.card.dto.request.CreateCardRequestDto;
import jpabook.trello_project.domain.card.dto.response.CardResponseDto;
import jpabook.trello_project.domain.card.dto.response.GetCardResponseDto;
import jpabook.trello_project.domain.card.dto.request.ModifyCardRequestDto;
import jpabook.trello_project.domain.card.entity.Card;
import jpabook.trello_project.domain.card.repository.CardRepository;
import jpabook.trello_project.domain.common.dto.AuthUser;
import jpabook.trello_project.domain.common.exceptions.InvalidRequestException;
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

    @Transactional
    public CardResponseDto createCard(CreateCardRequestDto requestDto, Long id, AuthUser authUser) {
        log.info("::: 카드 저장 로직 동작 :::");
        // 회원 로직 검사
        log.info("::: 회원 -> 멤버 검사 로직 동작 :::");

        log.info("::: 멤버 권한 로직 동작 :::");

        // lists 추가 해야함
        Card card = new Card(requestDto);
        Card createCard = cardRepository.save(card);
        log.info("::: 카드 저장 로직 완료 :::");
        return new CardResponseDto(createCard);
    }

    @Transactional
    public CardResponseDto modifyCard(Long id, ModifyCardRequestDto requestDto, AuthUser authUser) {
        log.info("::: 카드 수정 로직 동작 :::");

        Card card = checkCardExist(id);

        log.info("::: 회원 -> 멤버 검사 로직 동작 :::");

        log.info("::: 멤버 권한 로직 동작 :::");

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

        log.info("::: 카드 수정 조회 동작 :::");

        Card card = checkCardExist(id);

        log.info("::: 카드 수정 조회 완료 :::");
        return new GetCardResponseDto(card);
    }

    @Transactional
    public void deleteCard(Long id, AuthUser authUser) {
        log.info("::: 카드 삭제 로직 동작 :::");

        Card card = checkCardExist(id);

        log.info("::: 회원 -> 멤버 검사 로직 동작 :::");

        log.info("::: 멤버 권한 로직 동작 :::");

        cardRepository.deleteById(id);
        log.info("::: 카드 삭제 로직 완료 :::");
    }

    private Card checkCardExist(Long id) {
        log.info("::: 카드 존재 여부 검사 로직 동작 :::");
        return cardRepository.findById(id)
                .orElseThrow(() -> new InvalidRequestException("해당 카드가 없습니다!"));
    }

}
