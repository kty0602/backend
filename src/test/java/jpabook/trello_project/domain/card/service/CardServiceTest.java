package jpabook.trello_project.domain.card.service;

import jpabook.trello_project.domain.board.entity.Board;
import jpabook.trello_project.domain.board.repository.BoardRepository;
import jpabook.trello_project.domain.card.dto.request.CreateCardRequestDto;
import jpabook.trello_project.domain.card.dto.request.ModifyCardRequestDto;
import jpabook.trello_project.domain.card.entity.Card;
import jpabook.trello_project.domain.card.repository.CardRepository;
import jpabook.trello_project.domain.common.dto.AuthUser;
import jpabook.trello_project.domain.lists.entity.Lists;
import jpabook.trello_project.domain.lists.repository.ListRepository;
import jpabook.trello_project.domain.user.entity.User;
import jpabook.trello_project.domain.user.enums.UserRole;
import jpabook.trello_project.domain.user.repository.UserRepository;
import jpabook.trello_project.domain.workspace.entity.Workspace;
import jpabook.trello_project.domain.workspace.repository.WorkspaceRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Slf4j
@TestPropertySource(properties = {"spring.profiles.active=test"})
class CardServiceTest {

    @Autowired
    CardService cardService;

    @Autowired
    LockCardService lockCardService;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    UserRepository userRepository;
    @Autowired
    ListRepository listRepository;
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    WorkspaceRepository workspaceRepository;

    AuthUser authUser;

    @BeforeEach
    public void setUp() {
        authUser = new AuthUser(1L, "email@email.com", "name", UserRole.ROLE_ADMIN);
        User user = User.fromAuthUser(authUser);
        userRepository.save(user);
        Workspace workspace = new Workspace(user, "work title", "info");
        workspaceRepository.save(workspace);
        Board board = new Board(workspace, "board title", "blue", "imgUrl");
        boardRepository.save(board);
        Lists lists = new Lists(board, "lists title", 1);
        lists = listRepository.save(lists);

        CreateCardRequestDto dto = new CreateCardRequestDto("title", "info", LocalDate.now());
        Card card = new Card(dto, lists);
        cardRepository.save(card);
    }

    @AfterEach
    public void clear() {
        cardRepository.deleteAll();
    }

    @Test
    @Disabled
    public void saved_ok() {
        assertEquals(1, cardRepository.count());
        assertEquals("title", cardRepository.findById(1L).get().getTitle());
        assertEquals("info", cardRepository.findById(1L).get().getInfo());
    }

    @Test
    public void testWithoutLock() throws InterruptedException {
        long start = System.currentTimeMillis();

        int threadCount = 100;
        // 32개의 스레드를 가진 스레드 풀 생성
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            String newTitle = "title" + i;
            String newInfo = "info" + i;
            executorService.execute(() -> {
                try {
                    cardService.modifyCard(1L, new ModifyCardRequestDto(newTitle, newInfo, LocalDate.now()), 1L, authUser);
                } finally {
                    // 작업 마친 후 스레드 수 줄임.
                    latch.countDown();
                }
            });
        }

        latch.await();  // CountDownLatch 값 0이 될 때까지 대기
        executorService.shutdown();     // 스레드 풀 종료

        long end = System.currentTimeMillis();
        log.info("총 소요 시간 : {}", end - start);
        System.out.println("총 소요 시간 : " + (end - start));
    }
    
    @Test
    public void testWithPessimisticLock() throws InterruptedException {
        long start = System.currentTimeMillis();

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            String newTitle = "title" + i;
            String newInfo = "info" + i;
            executorService.execute(() -> {
                try {
                    lockCardService.modifyCardWithPessimisticLock(1L, new ModifyCardRequestDto(newTitle, newInfo, LocalDate.now()), 1L, authUser);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        long end = System.currentTimeMillis();
        log.info("총 소요 시간 : {}", end - start);
        System.out.println("총 소요 시간 : " + (end - start));
    }

    @Test
    public void testWithOptimisticLock() throws InterruptedException {
        long start = System.currentTimeMillis();

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            String newTitle = "title" + i;
            String newInfo = "info" + i;
            executorService.execute(() -> {
                try {
                    lockCardService.modifyCardWithOptimisticLock(1L, new ModifyCardRequestDto(newTitle, newInfo, LocalDate.now()), 1L, authUser);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        long end = System.currentTimeMillis();
        log.info("총 소요 시간 : {}", end - start);
        System.out.println("총 소요 시간 : " + (end - start));
    }
}