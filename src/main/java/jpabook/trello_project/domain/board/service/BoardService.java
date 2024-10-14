package jpabook.trello_project.domain.board.service;

import jpabook.trello_project.domain.board.dto.request.BoardRequestDto;
import jpabook.trello_project.domain.board.dto.response.BoardResponseDto;
import jpabook.trello_project.domain.board.entity.Board;
import jpabook.trello_project.domain.board.repository.BoardRepository;
import jpabook.trello_project.domain.common.dto.AuthUser;
import jpabook.trello_project.domain.common.exceptions.AccessDeniedException;
import jpabook.trello_project.domain.common.exceptions.InvalidTitleException;
import jpabook.trello_project.domain.user.entity.User;
import jpabook.trello_project.domain.workspace.entity.Workspace;
import jpabook.trello_project.domain.workspace.repository.WorkspaceRepository;
import jpabook.trello_project.domain.workspace_member.entity.WorkspaceMember;
import jpabook.trello_project.domain.workspace_member.enums.WorkRole;
import jpabook.trello_project.domain.workspace_member.repository.WorkspaceMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public BoardResponseDto createBoard(AuthUser authUser, Long workId, BoardRequestDto boardRequestDto) {

        if (boardRequestDto.getTitle() == null || boardRequestDto.getTitle().trim().isEmpty()) {
            throw new InvalidTitleException("제목이 비어있습니다.");
        }

        Workspace workspace = workspaceRepository.findById(workId)
                .orElseThrow(() -> new AccessDeniedException("워크스페이스를 찾을 수 없습니다."));


        WorkspaceMember workspaceMember = workspaceMemberRepository.findByUserAndWorkspace(User.fromAuthUser(authUser), workspace)
                .orElseThrow(() -> new AccessDeniedException("워크스페이스의 멤버가 아닙니다."));

        if (workspaceMember.getWorkRole() == WorkRole.ROLE_READONLY) {
            throw new AccessDeniedException("보드를 생성할 권한이 없습니다.");
        }

        Board newBoard = new Board(workspace, boardRequestDto.getTitle(), boardRequestDto.getBackgroundColor(), boardRequestDto.getImgUrl());
        Board savedBoard = boardRepository.save(newBoard);

        return new BoardResponseDto(savedBoard);
    }


    // 리스트랑 카드도 보이게 고치기
    public BoardResponseDto getOneBoard(AuthUser authUser, Long workId, Long boardId) {
        Workspace workspace = workspaceRepository.findById(workId)
                .orElseThrow(() -> new AccessDeniedException("워크스페이스를 찾을 수 없습니다."));

        WorkspaceMember workspaceMember = workspaceMemberRepository.findByUserAndWorkspace(User.fromAuthUser(authUser), workspace)
                .orElseThrow(() -> new AccessDeniedException("워크스페이스의 멤버가 아닙니다."));

        Board board = boardRepository.findByIdAndWorkspace(boardId, workspaceMember.getWorkspace())
                .orElseThrow(() -> new AccessDeniedException("보드를 찾을 수 없습니다."));

        return new BoardResponseDto(board);
    }


    public Page<BoardResponseDto> getBoards(int page, int size, AuthUser authUser, Long workId) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Workspace workspace = workspaceRepository.findById(workId)
                .orElseThrow(() -> new AccessDeniedException("워크스페이스를 찾을 수 없습니다."));

        WorkspaceMember workspaceMember = workspaceMemberRepository.findByUserAndWorkspace(User.fromAuthUser(authUser), workspace)
                .orElseThrow(() -> new AccessDeniedException("워크스페이스의 멤버가 아닙니다."));

        Page<Board> boards = boardRepository.findByWorkspace(workspaceMember.getWorkspace(), pageable);

        return boards.map(BoardResponseDto::new);

    }


    @Transactional
    public BoardResponseDto updateBoard(AuthUser authUser, Long workId, Long boardId, BoardRequestDto boardRequestDto) {
        Workspace workspace = workspaceRepository.findById(workId)
                .orElseThrow(() -> new AccessDeniedException("워크스페이스를 찾을 수 없습니다."));


        WorkspaceMember workspaceMember = workspaceMemberRepository.findByUserAndWorkspace(User.fromAuthUser(authUser), workspace)
                .orElseThrow(() -> new AccessDeniedException("워크스페이스의 멤버가 아닙니다."));

        if (workspaceMember.getWorkRole() == WorkRole.ROLE_READONLY) {
            throw new AccessDeniedException("보드를 수정할 권한이 없습니다.");
        }

        Board board = boardRepository.findByIdAndWorkspace(boardId, workspace)
                .orElseThrow(() -> new AccessDeniedException("보드를 찾을 수 없습니다."));

        Board updatedBoard = board.update(boardRequestDto);

        return new BoardResponseDto(updatedBoard);
    }
    @Transactional
    public void deleteBoard(AuthUser authUser, Long workId, Long boardId) {
        Workspace workspace = workspaceRepository.findById(workId)
                .orElseThrow(() -> new AccessDeniedException("워크스페이스를 찾을 수 없습니다."));

        WorkspaceMember workspaceMember = workspaceMemberRepository.findByUserAndWorkspace(User.fromAuthUser(authUser), workspace)
                .orElseThrow(() -> new AccessDeniedException("워크스페이스의 멤버가 아닙니다."));

        Board board = boardRepository.findByIdAndWorkspace(boardId, workspaceMember.getWorkspace())
                .orElseThrow(() -> new AccessDeniedException("보드를 찾을 수 없습니다."));

        if(workspaceMember.getWorkRole() == WorkRole.ROLE_READONLY) {
            throw new AccessDeniedException("보드를 삭제할 권한이 없습니다.");
        }

        boardRepository.delete(board);
    }
}
