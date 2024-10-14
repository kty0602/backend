package jpabook.trello_project.domain.auth.service;

import jpabook.trello_project.config.JwtUtil;
import jpabook.trello_project.domain.auth.dto.request.SigninRequest;
import jpabook.trello_project.domain.auth.dto.request.SignupRequest;
import jpabook.trello_project.domain.auth.dto.response.SigninResponse;
import jpabook.trello_project.domain.auth.dto.response.SignupResponse;
import jpabook.trello_project.domain.common.exceptions.ApiException;
import jpabook.trello_project.domain.common.exceptions.ErrorStatus;
import jpabook.trello_project.domain.common.exceptions.InvalidRequestException;
import jpabook.trello_project.domain.user.entity.User;
import jpabook.trello_project.domain.user.enums.UserRole;
import jpabook.trello_project.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignupResponse signup(SignupRequest signupRequest) {

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new ApiException(ErrorStatus._DUPLICATED_EMAIL);
        }

        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        UserRole userRole = UserRole.of(signupRequest.getUserRole());

        User newUser = new User(
                signupRequest.getEmail(),
                signupRequest.getName(),
                encodedPassword,
                userRole
        );
        User savedUser = userRepository.save(newUser);

        String bearerToken = jwtUtil.createToken(savedUser.getId(), savedUser.getEmail(), userRole);

        return new SignupResponse(bearerToken);
    }

    public SigninResponse signin(SigninRequest signinRequest) {
        User user = userRepository.findByEmail(signinRequest.getEmail()).orElseThrow(
                () -> new ApiException(ErrorStatus._NOT_FOUND_USER));

        // 로그인 시 이메일과 비밀번호가 일치하지 않을 경우 401을 반환합니다.
        if (!passwordEncoder.matches(signinRequest.getPassword(), user.getPassword())) {
            throw new ApiException(ErrorStatus._BAD_REQUEST_PASSWORD);
        }

        String bearerToken = jwtUtil.createToken(user.getId(), user.getEmail(), user.getUserRole());

        return new SigninResponse(bearerToken);
    }
}
