package jpabook.trello_project.domain.card.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Transactional
class CardServiceTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Rollback(value = false)
    public void generateAndSaveCardWithJdbcTemplate() {
        int batchSize = 10000;  // 한번에 저장할 배치 크기
        int totalCards = 1000000; // 총 생성할 유저 수

        List<Object[]> batchArgs = new ArrayList<>();

        for (int i = 0; i < totalCards; i++) {


            batchArgs.add(new Object[]{LocalDate.now(), "info", "title", 1, 1});

            // 배치마다 insert 실행
            if (batchArgs.size() == batchSize) {
                jdbcTemplate.batchUpdate("INSERT INTO card (due, info, title, lists_id, rnk) VALUES (?, ?, ?, ?)", batchArgs);
                batchArgs.clear(); // 배치 완료 후 리스트 초기화
            }
        }

        // 남은 카드 저장
        if (!batchArgs.isEmpty()) {
            jdbcTemplate.batchUpdate("INSERT INTO card (due, info, title, lists_id, rnk) VALUES (?, ?, ?, ?)", batchArgs);
        }
    }
}