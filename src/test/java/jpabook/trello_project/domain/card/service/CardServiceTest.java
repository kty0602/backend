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
import jpabook.trello_project.domain.workspace_member.entity.WorkspaceMember;
import jpabook.trello_project.domain.workspace_member.enums.WorkRole;
import jpabook.trello_project.domain.workspace_member.repository.WorkspaceMemberRepository;
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
    @Autowired
    WorkspaceMemberRepository workspaceMemberRepository;

    int threadCount = 5;

    AuthUser authUser;
    User savedUser;
    Workspace savedWorkspace;
    Board savedBoard;
    Lists savedLists;
    Card savedCard;

    @BeforeEach
    public void setUp() {
        authUser = new AuthUser(1L, "email@email.com", "name", UserRole.ROLE_ADMIN);
        User user = User.fromAuthUser(authUser);
        savedUser = userRepository.save(user);
        Workspace workspace = new Workspace(savedUser, "work title", "info");
        savedWorkspace = workspaceRepository.save(workspace);
        Board board = new Board(savedWorkspace, "board title", "blue", "imgUrl");
        savedBoard = boardRepository.save(board);
        Lists lists = new Lists(savedBoard, "lists title", 1);
        savedLists = listRepository.save(lists);
        workspaceMemberRepository.save(new WorkspaceMember(savedUser, savedWorkspace, WorkRole.ROLE_WORKSPACE));

        CreateCardRequestDto dto = new CreateCardRequestDto("title", "info", LocalDate.now());
        Card card = new Card(dto, savedLists);
        savedCard = cardRepository.save(card);
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

        // 32개의 스레드를 가진 스레드 풀 생성
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            String newTitle = "title" + i;
            String newInfo = "info" + i;
            executorService.execute(() -> {
                try {
                    cardService.modifyCard(
                            savedCard.getId(),
                            new ModifyCardRequestDto(newTitle, newInfo, LocalDate.now()),
                            savedWorkspace.getId(),
                            authUser
                    );
                } finally {
                    // 작업 마친 후 스레드 수 줄임.
                    latch.countDown();
                }
            });
        }

        latch.await();  // CountDownLatch 값 0이 될 때까지 대기
        executorService.shutdown();     // 스레드 풀 종료

        long end = System.currentTimeMillis();
        log.info("::::: 총 소요 시간 : {} :::::", end - start);
    }
    
    @Test
    public void testWithPessimisticLock() throws InterruptedException {
        long start = System.currentTimeMillis();

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            String newTitle = "title" + i;
            String newInfo = "info" + i;
            executorService.execute(() -> {
                try {
                    lockCardService.modifyCardWithPessimisticLock(
                            savedCard.getId(),
                            new ModifyCardRequestDto(newTitle, newInfo, LocalDate.now()),
                            savedWorkspace.getId(),
                            authUser
                    );
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        long end = System.currentTimeMillis();
        log.info("::::: 총 소요 시간 : {} :::::", end - start);
    }

    @Test
    public void testWithOptimisticLock() throws InterruptedException {
        long start = System.currentTimeMillis();
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            String newTitle = "title" + i;
            String newInfo = "info" + i;
            executorService.execute(() -> {
                try {
                    lockCardService.modifyCardWithOptimisticLock(
                            savedCard.getId(),
                            new ModifyCardRequestDto(newTitle, newInfo, LocalDate.now()),
                            savedWorkspace.getId(),
                            authUser
                    );
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        long end = System.currentTimeMillis();
        log.info("::::: 총 소요 시간 : {} :::::", end - start);
    }

    @Test
    public void testWithNamedLock() throws InterruptedException {
        long start = System.currentTimeMillis();

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            String newTitle = "title" + i;
            String newInfo = "info" + i;
            executorService.execute(() -> {
                try {
                    lockCardService.modifyCardWithNamedLock(
                            savedCard.getId(),
                            new ModifyCardRequestDto(newTitle, newInfo, LocalDate.now()),
                            savedWorkspace.getId(),
                            authUser
                    );
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        long end = System.currentTimeMillis();
        log.info("::::: 총 소요 시간 : {} :::::", end - start);
    }
}