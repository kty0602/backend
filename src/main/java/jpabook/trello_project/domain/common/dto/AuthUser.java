package jpabook.trello_project.domain.common.dto;

import jpabook.trello_project.domain.user.enums.UserRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@Getter
public class AuthUser {
    private final Long id;
    private final String email;
    private final String name;
    private final Collection<? extends GrantedAuthority> authorities;

    public AuthUser(Long id, String email, String name, UserRole role) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.authorities = List.of(new SimpleGrantedAuthority(role.name()));
    }
}

