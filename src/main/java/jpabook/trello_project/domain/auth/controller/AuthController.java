package jpabook.trello_project.domain.auth.controller;

import jakarta.validation.Valid;
import jpabook.trello_project.domain.auth.dto.request.SigninRequest;
import jpabook.trello_project.domain.auth.dto.request.SignupRequest;
import jpabook.trello_project.domain.auth.dto.response.SigninResponse;
import jpabook.trello_project.domain.auth.dto.response.SignupResponse;
import jpabook.trello_project.domain.auth.service.AuthService;
import jpabook.trello_project.domain.common.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/signup")
    public ResponseEntity<ResponseDto<SignupResponse>> signup(@Valid @RequestBody SignupRequest signupRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.of(HttpStatus.CREATED, authService.signup(signupRequest)));
    }

    @PostMapping("/auth/signin")
    public ResponseDto<SigninResponse> signin(@Valid @RequestBody SigninRequest signinRequest) {
        return ResponseDto.of(HttpStatus.OK, authService.signin(signinRequest));
    }
}
