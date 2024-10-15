package jpabook.trello_project.domain.user.controller;

import jakarta.validation.Valid;
import jpabook.trello_project.domain.common.dto.AuthUser;
import jpabook.trello_project.domain.common.dto.ResponseDto;
import jpabook.trello_project.domain.user.dto.request.WithdrawRequest;
import jpabook.trello_project.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @DeleteMapping
    public ResponseDto<Object> withdraw(@AuthenticationPrincipal AuthUser authUser,
                                        @Valid @RequestBody WithdrawRequest withdrawRequest) {
        userService.withdraw(authUser, withdrawRequest);
        return ResponseDto.of(HttpStatus.OK, "회원 탈퇴가 정상적으로 완료되었습니다.");
    }
}
