package jpabook.trello_project.domain.lists.service;

import jpabook.trello_project.domain.board.entity.Board;
import jpabook.trello_project.domain.board.repository.BoardRepository;
import jpabook.trello_project.domain.common.dto.AuthUser;
import jpabook.trello_project.domain.common.exceptions.ApiException;
import jpabook.trello_project.domain.common.exceptions.ErrorStatus;
import jpabook.trello_project.domain.lists.dto.*;
import jpabook.trello_project.domain.lists.entity.Lists;
import jpabook.trello_project.domain.lists.repository.ListRepository;
import jpabook.trello_project.domain.workspace.repository.WorkspaceRepository;
import jpabook.trello_project.domain.workspace_member.entity.WorkspaceMember;
import jpabook.trello_project.domain.workspace_member.enums.WorkRole;
import jpabook.trello_project.domain.workspace_member.repository.WorkspaceMemberRepository;
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

    private final WorkspaceRepository workspaceRepository;
    private final BoardRepository boardRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    private void checkIfValidUser(AuthUser user, Long workId) {
        WorkspaceMember wm = workspaceMemberRepository.findByUserIdAndWorkspaceId(user.getId(), workId)
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_MEMBER));
        if(wm.getWorkRole() == WorkRole.ROLE_READONLY)
            throw new ApiException(ErrorStatus._UNAUTHORIZED_USER);
    }

    private void checkIfExist(Long workId, Long boardId) {
        workspaceRepository.findById(workId).orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_WORKSPACE));
        boardRepository.findById(boardId).orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_BOARD));
    }

    private Lists findList(Long boardId, Long listId) {
        return listRepository.findByIdAndBoardId(listId, boardId)
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_LIST));
    }

    @Transactional
    public ListSaveResponseDto saveList(AuthUser user, Long workId, Long boardId, ListSaveRequestDto requestDto) {
        checkIfValidUser(user, workId);
        workspaceRepository.findById(workId).orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_WORKSPACE));
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_BOARD));
        Lists savedList = listRepository.save(new Lists(board, requestDto.getTitle(), listRepository.findMaxListOrder() + 1));
        return new ListSaveResponseDto(savedList);
    }

    public ListResponseDto findList(Long workId, Long boardId, Long listId) {
        checkIfExist(workId, boardId);
        Lists list = findList(boardId, listId);
        return new ListResponseDto(list);
    }

    public List<ListResponseDto> getLists(Long workId, Long boardId) {
        checkIfExist(workId, boardId);
        List<ListResponseDto> responseDtos = listRepository.findAllByBoardId(boardId).stream()
                .map(ListResponseDto::new)
                .collect(Collectors.toList());
        return responseDtos;
    }

    @Transactional
    public ListResponseDto changeList(AuthUser user, Long workId, Long boardId, Long listId, ListChangeRequestDto changeDto) {
        checkIfValidUser(user, workId);
        checkIfExist(workId, boardId);
        Lists list = findList(boardId, listId);
        list.changeTitle(changeDto.getNewTitle());
        return new ListResponseDto(list);
    }

    @Transactional
    public String changeListOrder(AuthUser user, Long workId, Long boardId, Long listId, ListOrderChangeRequestDto orderDto) {
        checkIfValidUser(user, workId);
        checkIfExist(workId, boardId);
        Lists list = findList(boardId, listId);

        long newOrder = orderDto.getNewListOrder();
        long curOrder = list.getListOrder();
        if (newOrder < curOrder) {  // 순서를 앞으로 이동하는 경우
            listRepository.increaseListOrderBetween(newOrder, curOrder);
        } else { // 순서를 뒤로 이동하는 경우
            listRepository.decreaseListOrderBetween(newOrder, curOrder);
        }

        list.changeListOrder(orderDto.getNewListOrder());
        return "리스트 순서가 변경되었습니다.";
    }

    @Transactional
    public String deleteList(AuthUser user, Long workId, Long boardId, Long listId) {
        checkIfValidUser(user, workId);
        checkIfExist(workId, boardId);
        Lists list = findList(boardId, listId);
        listRepository.delete(list);
        return "리스트 삭제가 완료되었습니다.";
    }
}
