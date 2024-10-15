package jpabook.trello_project.domain.user.service;

import jpabook.trello_project.domain.common.dto.AuthUser;
import jpabook.trello_project.domain.common.exceptions.ApiException;
import jpabook.trello_project.domain.common.exceptions.ErrorStatus;
import jpabook.trello_project.domain.user.dto.request.WithdrawRequest;
import jpabook.trello_project.domain.user.entity.User;
import jpabook.trello_project.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void withdraw(AuthUser authUser, WithdrawRequest withdrawRequest) {
        // 로그인 회원과 탈퇴를 시도하는 회원이 다를 때
        if (!authUser.getEmail().equals(withdrawRequest.getEmail())) {
            throw new ApiException(ErrorStatus._UNAUTHORIZED);
        }

        User user = findByEmail(authUser.getEmail());

        // 비밀번호 불일치
        if (!passwordEncoder.matches(withdrawRequest.getPassword(), user.getPassword())) {
            throw new ApiException(ErrorStatus._UNAUTHORIZED_SIGNIN);
        }

        user.withdraw();
    }

    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new ApiException(ErrorStatus._NOT_FOUND_USER));

        if (user.isDeleted()) {
            throw new ApiException(ErrorStatus._BAD_REQUEST_USER);
        }

        return user;
    }

    public User findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ApiException(ErrorStatus._NOT_FOUND_USER));

        if (user.isDeleted()) {
            throw new ApiException(ErrorStatus._BAD_REQUEST_USER);
        }

        return user;
    }
}
