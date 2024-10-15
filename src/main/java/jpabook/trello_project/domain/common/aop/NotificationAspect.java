package jpabook.trello_project.domain.common.aop;

import jpabook.trello_project.client.SlackNotifier;
import jpabook.trello_project.domain.board.dto.response.BoardResponseDto;
import jpabook.trello_project.domain.board.entity.Board;
import jpabook.trello_project.domain.common.dto.ResponseDto;
import jpabook.trello_project.domain.reply.dto.response.ReplyResponseDto;
import jpabook.trello_project.domain.workspace_member.dto.response.WorkspaceMemberResponseDto;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class NotificationAspect {

    private final SlackNotifier slackNotifier;

    @AfterReturning(value = "execution(* jpabook.trello_project.domain.board.controller.BoardController.createBoard(..))",returning = "responseEntity")
    public void afterBoardCreated(ResponseEntity<ResponseDto<BoardResponseDto>> responseEntity) {
        ResponseDto<BoardResponseDto> responseDto = responseEntity.getBody();
        if(responseDto != null) {
            BoardResponseDto board = responseDto.getData();
            String message = String.format(
                    "A new board has been created!\n" +
                            "Board id: %s\n" +
                            "Board title: %s\n",
                    board.getBoardId(), board.getTitle()
            );
            slackNotifier.sendSlackMessage(message);
        }
    }

    @AfterReturning(value = "execution(* jpabook.trello_project.domain.workspace.controller.WorkspaceController.inviteMember(..))",returning = "responseEntity")
    public void afterMemberAdded(ResponseEntity<ResponseDto<WorkspaceMemberResponseDto>> responseEntity) {
        ResponseDto<WorkspaceMemberResponseDto> responseDto = responseEntity.getBody();
        if(responseDto != null) {
            WorkspaceMemberResponseDto workspaceMember = responseDto.getData();
            String message = String.format(
                    "A new member has been added!\n" +
                    "Workspace id: %s\n" +
                    "Added member id: %s\n",
                    workspaceMember.getWorkId(), workspaceMember.getMemberId()
            );
            slackNotifier.sendSlackMessage(message);
        }
    }

    @AfterReturning(value = "execution(* jpabook.trello_project.domain.reply.controller.ReplyController.createReply(..))", returning = "responseEntity")
    public void afterCommentAdded(ResponseEntity<ResponseDto<ReplyResponseDto>> responseEntity) {
        ResponseDto<ReplyResponseDto> responseDto = responseEntity.getBody();
        if(responseDto != null) {
            ReplyResponseDto reply = responseDto.getData();
            String message = String.format(
                    "A new reply has been added!\n" +
                            "User Id: %s\n" +
                            "Card Id: %s\n" +
                            "Comment: %s\n",
                    reply.getUserId(), reply.getCardId(), reply.getContent()
            );
            slackNotifier.sendSlackMessage(message);
        }
    }
}
