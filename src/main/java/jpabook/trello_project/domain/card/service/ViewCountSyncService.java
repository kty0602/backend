package jpabook.trello_project.domain.card.service;

import jpabook.trello_project.domain.card.entity.Card;
import jpabook.trello_project.domain.card.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static jpabook.trello_project.domain.card.service.CardService.CARD_TOP_RANKINGS;

@Service
@RequiredArgsConstructor
public class ViewCountSyncService {

    private final RedisTemplate<String, String> redisTemplate;
    private final CardRepository cardRepository;
    private final CardService cardService;

    // 캐시에 있는 카드 조회수를 1분마다 db에 반영
    @Scheduled(fixedRate = 60000) // 1분마다 실행
    @Transactional
    public void syncViewCountsToDB() {
        Set<String> keys = redisTemplate.keys(CardService.CARD_VIEW_COUNT_PREFIX + "*");
        if (keys != null) {
            for (String key : keys) {
                Long cardId = extractPostIdFromKey(key);
                String viewCountStr = redisTemplate.opsForValue().get(key);
                if (viewCountStr != null) {
                    Long viewCount = Long.parseLong(viewCountStr);
                    Card card = cardRepository.findById(cardId).orElse(null);
                    // 디비에 조회수 반영
                    if (card != null) card.changeViewCount(viewCount);
                    // zset 에 조회수 반영
                    redisTemplate.opsForZSet()
                            .add(CARD_TOP_RANKINGS, cardId.toString(), viewCount.doubleValue());
                }
            }
        }
        cardService.loadCardRankingFromDBToRedis();
    }

    // 매일 자정에 조회수 초기화
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void resetCardViewCounts() {
        cardRepository.resetAllViewCounts();
    }

    private Long extractPostIdFromKey(String key) {
        return Long.parseLong(key.split(":")[2]);
    }
}
