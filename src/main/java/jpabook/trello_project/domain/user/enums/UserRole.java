package jpabook.trello_project.domain.user.enums;

import jpabook.trello_project.domain.common.exceptions.ApiException;
import jpabook.trello_project.domain.common.exceptions.ErrorStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;


@Getter
@RequiredArgsConstructor
public enum UserRole {
    ROLE_USER(Authority.USER),
    ROLE_ADMIN(Authority.ADMIN);

    private final String userRole;

    public static UserRole of(String role) {
        return Arrays.stream(UserRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new ApiException(ErrorStatus._BAD_REQUEST_USER_ROLE));
    }

    public static class Authority {
        public static final String USER = "ROLE_USER";
        public static final String ADMIN = "ROLE_ADMIN";
    }
}
