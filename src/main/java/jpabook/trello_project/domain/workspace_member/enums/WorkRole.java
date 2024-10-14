package jpabook.trello_project.domain.workspace_member.enums;

import jpabook.trello_project.domain.common.exceptions.InvalidRequestException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum WorkRole {
    ROLE_WORKSPACE(WorkRole.Authority.WORKSPACE),
    ROLE_BOARD(WorkRole.Authority.BOARD),
    ROLE_READONLY(WorkRole.Authority.READONLY);

    private final String userRole;

    public static WorkRole of(String role) {
        return Arrays.stream(WorkRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new InvalidRequestException("유효하지 않은 WorkRole"));
    }

    public static class Authority {
        public static final String WORKSPACE = "ROLE_WORKSPACE";
        public static final String BOARD = "ROLE_BOARD";
        public static final String READONLY = "ROLE_READONLY";
    }
}
