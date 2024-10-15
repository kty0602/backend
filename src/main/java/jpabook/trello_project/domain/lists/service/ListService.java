package jpabook.trello_project.domain.lists.service;

import jpabook.trello_project.domain.board.BoardRepository;
import jpabook.trello_project.domain.board.entity.Board;
import jpabook.trello_project.domain.common.dto.AuthUser;
import jpabook.trello_project.domain.common.exceptions.ApiException;
import jpabook.trello_project.domain.common.exceptions.ErrorStatus;
import jpabook.trello_project.domain.lists.dto.*;
import jpabook.trello_project.domain.lists.entity.Lists;
import jpabook.trello_project.domain.lists.repository.ListRepository;
import jpabook.trello_project.domain.workspace.WorkSpaceRepository;
import jpabook.trello_project.domain.workspace_member.WorkspaceMemberRepository;
import jpabook.trello_project.domain.workspace_member.entity.WorkspaceMember;
import jpabook.trello_project.domain.workspace_member.enums.WorkRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListService {
    private final ListRepository listRepository;

    private final WorkSpaceRepository workspaceRepository;  // 교체
    private final BoardRepository boardRepository;  // 교체
    private final WorkspaceMemberRepository workspaceMemberRepository;  // 교체

    private void checkIfValidUser(AuthUser user, Long workId) {
        WorkspaceMember wm = workspaceMemberRepository.findByUserIdAndWorkId(user.getId(), workId)
                .orElseThrow(() -> new ApiException(ErrorStatus._INVALID_WORK_ROLE));
        if(wm.getWorkRole() == WorkRole.ROLE_READONLY)
            throw new ApiException(ErrorStatus._INVALID_WORK_ROLE);
    }

    private void checkIfExist(Long workId, Long boardId) {
        workspaceRepository.findById(workId).orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_WORKSPACE));
        boardRepository.findById(boardId).orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_BOARD));
    }

    @Transactional
    public ListSaveResponseDto saveList(AuthUser user, Long workId, Long boardId, ListSaveRequestDto requestDto) {
        checkIfValidUser(user, workId);
        workspaceRepository.findById(workId).orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_WORKSPACE));
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_BOARD));
        Lists savedList = listRepository.save(new Lists(board, requestDto.getTitle(), listRepository.findMaxListOrder() + 1));
        return new ListSaveResponseDto(savedList);
    }

    public ListResponseDto getList(Long workId, Long boardId, Long listId) {
        checkIfExist(workId, boardId);
        Lists list = listRepository.findById(listId).orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_LIST));
        return new ListResponseDto(list);
    }

    public List<ListResponseDto> getLists(Long workId, Long boardId) {
        checkIfExist(workId, boardId);
        List<ListResponseDto> responseDtos = listRepository.findAll().stream()
                .map(ListResponseDto::new)
                .collect(Collectors.toList());
        return responseDtos;
    }

    @Transactional
    public ListResponseDto changeList(AuthUser user, Long workId, Long boardId, Long listId, ListChangeRequestDto changeDto) {
        checkIfValidUser(user, workId);
        checkIfExist(workId, boardId);
        Lists list = listRepository.findById(listId).orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_LIST));
        list.changeTitle(changeDto.getNewTitle());
        return new ListResponseDto(list);
    }

    @Transactional
    public String changeListOrder(AuthUser user, Long workId, Long boardId, Long listId, ListOrderChangeRequestDto orderDto) {
        checkIfValidUser(user, workId);
        checkIfExist(workId, boardId);
        Lists list = listRepository.findById(listId).orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_LIST));
        list.changeListOrder(orderDto.getNewListOrder());
        listRepository.updateListOrders(list.getListOrder());
        return "리스트 순서가 변경되었습니다.";
    }

    @Transactional
    public String deleteList(AuthUser user, Long workId, Long boardId, Long listId) {
        checkIfValidUser(user, workId);
        checkIfExist(workId, boardId);
        listRepository.deleteById(listId);
        return "리스트 삭제가 완료되었습니다.";
    }
}
