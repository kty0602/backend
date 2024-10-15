package jpabook.trello_project.domain.board.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import jpabook.trello_project.domain.board.dto.request.BoardRequestDto;
import jpabook.trello_project.domain.board.dto.response.BoardResponseDto;
import jpabook.trello_project.domain.board.dto.response.OneBoardResponseDto;
import jpabook.trello_project.domain.board.entity.Board;
import jpabook.trello_project.domain.board.repository.BoardRepository;
import jpabook.trello_project.domain.card.dto.response.CardResponseDto;
import jpabook.trello_project.domain.card.entity.Card;
import jpabook.trello_project.domain.card.repository.CardRepository;
import jpabook.trello_project.domain.common.dto.AuthUser;
import jpabook.trello_project.domain.common.exceptions.AccessDeniedException;
import jpabook.trello_project.domain.common.exceptions.InvalidTitleException;
import jpabook.trello_project.domain.lists.dto.ListCardResponseDto;
import jpabook.trello_project.domain.lists.entity.Lists;
import jpabook.trello_project.domain.lists.repository.ListRepository;
import jpabook.trello_project.domain.user.entity.User;
import jpabook.trello_project.domain.workspace.entity.Workspace;
import jpabook.trello_project.domain.workspace.repository.WorkspaceRepository;
import jpabook.trello_project.domain.workspace_member.entity.WorkspaceMember;
import jpabook.trello_project.domain.workspace_member.enums.WorkRole;
import jpabook.trello_project.domain.workspace_member.repository.WorkspaceMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final AmazonS3Client amazonS3Client;

    @Value("${S3_NAME}")
    private String bucket;

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final BoardRepository boardRepository;
    private final ListRepository listRepository;
    private final CardRepository cardRepository;

    @Transactional
    public BoardResponseDto createBoard(AuthUser authUser, Long workId, BoardRequestDto boardRequestDto, MultipartFile backgroundImageFile) throws IOException {

        // 제목 유효성 검사
        if (boardRequestDto.getTitle() == null || boardRequestDto.getTitle().trim().isEmpty()) {
            throw new InvalidTitleException("제목이 비어있습니다.");
        }

        // 워크스페이스 및 멤버 권한 확인
        Workspace workspace = workspaceRepository.findById(workId)
                .orElseThrow(() -> new AccessDeniedException("워크스페이스를 찾을 수 없습니다."));

        WorkspaceMember workspaceMember = workspaceMemberRepository.findByUserAndWorkspace(User.fromAuthUser(authUser), workspace)
                .orElseThrow(() -> new AccessDeniedException("워크스페이스의 멤버가 아닙니다."));

        if (workspaceMember.getWorkRole() == WorkRole.ROLE_READONLY) {
            throw new AccessDeniedException("보드를 생성할 권한이 없습니다.");
        }

        // 배경 이미지 업로드 처리
        String backgroundImageUrl = null;
        if (backgroundImageFile != null && !backgroundImageFile.isEmpty()) {
            backgroundImageUrl = uploadImageToS3(backgroundImageFile);
        }

        // 새로운 보드 생성 및 저장
        Board newBoard = new Board(workspace, boardRequestDto.getTitle(), boardRequestDto.getBackgroundColor(), backgroundImageUrl);
        Board savedBoard = boardRepository.save(newBoard);

        return new BoardResponseDto(savedBoard);
    }



    // Fetch details for a single board, including its lists and cards
    public OneBoardResponseDto getOneBoard(AuthUser authUser, Long workId, Long boardId) {
        Workspace workspace = workspaceRepository.findById(workId)
                .orElseThrow(() -> new AccessDeniedException("워크스페이스를 찾을 수 없습니다."));

        WorkspaceMember workspaceMember = workspaceMemberRepository.findByUserAndWorkspace(User.fromAuthUser(authUser), workspace)
                .orElseThrow(() -> new AccessDeniedException("워크스페이스의 멤버가 아닙니다."));

        Board board = boardRepository.findByIdAndWorkspace(boardId, workspaceMember.getWorkspace())
                .orElseThrow(() -> new AccessDeniedException("보드를 찾을 수 없습니다."));

        List<Lists> newListList = listRepository.findByBoard(board);

        List<ListCardResponseDto> listCardResponseDtos = newListList.stream()
                .map(list -> {
                    List<Card> cards = cardRepository.findByList(list);
                    List<CardResponseDto> cardResponseDtos = cards.stream()
                            .map(CardResponseDto::new)
                            .collect(Collectors.toList());
                    return new ListCardResponseDto(list, cardResponseDtos);
                })
                .collect(Collectors.toList());

        return new OneBoardResponseDto(board, listCardResponseDtos);
    }

    // Get a list of all boards in a workspace with pagination
    public Page<BoardResponseDto> getBoards(int page, int size, AuthUser authUser, Long workId) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Workspace workspace = workspaceRepository.findById(workId)
                .orElseThrow(() -> new AccessDeniedException("워크스페이스를 찾을 수 없습니다."));

        WorkspaceMember workspaceMember = workspaceMemberRepository.findByUserAndWorkspace(User.fromAuthUser(authUser), workspace)
                .orElseThrow(() -> new AccessDeniedException("워크스페이스의 멤버가 아닙니다."));

        Page<Board> boards = boardRepository.findByWorkspace(workspaceMember.getWorkspace(), pageable);

        return boards.map(BoardResponseDto::new);
    }

    // Update a board's title, background, and image
    @Transactional
    public BoardResponseDto updateBoard(AuthUser authUser, Long workId, Long boardId, BoardRequestDto boardRequestDto, MultipartFile backgroundImageFile) throws IOException {
        Workspace workspace = workspaceRepository.findById(workId)
                .orElseThrow(() -> new AccessDeniedException("워크스페이스를 찾을 수 없습니다."));

        WorkspaceMember workspaceMember = workspaceMemberRepository.findByUserAndWorkspace(User.fromAuthUser(authUser), workspace)
                .orElseThrow(() -> new AccessDeniedException("워크스페이스의 멤버가 아닙니다."));

        if (workspaceMember.getWorkRole() == WorkRole.ROLE_READONLY) {
            throw new AccessDeniedException("보드를 수정할 권한이 없습니다.");
        }

        Board board = boardRepository.findByIdAndWorkspace(boardId, workspace)
                .orElseThrow(() -> new AccessDeniedException("보드를 찾을 수 없습니다."));

        // S3에 배경 이미지 업로드 처리
        String backgroundImageUrl = board.getImgUrl(); // 기존 이미지 URL 가져오기

        if (backgroundImageFile != null && !backgroundImageFile.isEmpty()) {
            // 새 이미지가 업로드된 경우 기존 이미지를 S3에서 삭제
            if (backgroundImageUrl != null && !backgroundImageUrl.isEmpty()) {
                deleteImageFromS3(backgroundImageUrl);
            }

            // 새 이미지를 S3에 업로드
            backgroundImageUrl = uploadImageToS3(backgroundImageFile);
        }

        // 새 이미지 URL과 함께 보드 정보 업데이트
        Board updatedBoard = board.update(boardRequestDto, backgroundImageUrl);
        return new BoardResponseDto(updatedBoard);
    }


    // Delete a board
    @Transactional
    public void deleteBoard(AuthUser authUser, Long workId, Long boardId) {
        Workspace workspace = workspaceRepository.findById(workId)
                .orElseThrow(() -> new AccessDeniedException("워크스페이스를 찾을 수 없습니다."));

        WorkspaceMember workspaceMember = workspaceMemberRepository.findByUserAndWorkspace(User.fromAuthUser(authUser), workspace)
                .orElseThrow(() -> new AccessDeniedException("워크스페이스의 멤버가 아닙니다."));

        Board board = boardRepository.findByIdAndWorkspace(boardId, workspaceMember.getWorkspace())
                .orElseThrow(() -> new AccessDeniedException("보드를 찾을 수 없습니다."));

        if (workspaceMember.getWorkRole() == WorkRole.ROLE_READONLY) {
            throw new AccessDeniedException("보드를 삭제할 권한이 없습니다.");
        }

        boardRepository.delete(board);
    }

    private String uploadImageToS3(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String fileUrl = "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/" + fileName;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        amazonS3Client.putObject(bucket, fileName, file.getInputStream(), metadata);

        return fileUrl;
    }

    private void deleteImageFromS3(String imageUrl) {
        // URL에서 파일명 추출
        String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

        amazonS3Client.deleteObject(bucket, fileName);
    }
}
