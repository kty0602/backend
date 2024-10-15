package jpabook.trello_project.domain.auth.service;

import jpabook.trello_project.config.JwtUtil;
import jpabook.trello_project.domain.auth.dto.request.SigninRequest;
import jpabook.trello_project.domain.auth.dto.request.SignupRequest;
import jpabook.trello_project.domain.auth.dto.response.SigninResponse;
import jpabook.trello_project.domain.auth.dto.response.SignupResponse;
import jpabook.trello_project.domain.common.exceptions.ApiException;
import jpabook.trello_project.domain.common.exceptions.ErrorStatus;
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
        // 로그인 실패시 401 반환
        User user = userRepository.findByEmail(signinRequest.getEmail()).orElseThrow(
                () -> new ApiException(ErrorStatus._UNAUTHORIZED_SIGNIN));

        if (user.isDeleted()) throw new ApiException(ErrorStatus._BAD_REQUEST_USER);

        if (!passwordEncoder.matches(signinRequest.getPassword(), user.getPassword())) {
            throw new ApiException(ErrorStatus._UNAUTHORIZED_SIGNIN);
        }

        String bearerToken = jwtUtil.createToken(user.getId(), user.getEmail(), user.getUserRole());

        return new SigninResponse(bearerToken);
    }
}
