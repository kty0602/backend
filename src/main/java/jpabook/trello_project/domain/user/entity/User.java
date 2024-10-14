package jpabook.trello_project.domain.user.entity;

import jakarta.persistence.*;
import jpabook.trello_project.domain.common.dto.AuthUser;
import jpabook.trello_project.domain.user.enums.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @Column(unique = true)
    private String email;
    private String name;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRole userRole;
    private boolean isDeleted = false;

    private User(Long id, String name, String email, UserRole userRole) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.userRole = userRole;
    }

    public static User fromAuthUser(AuthUser authUser) {
        return new User(authUser.getId(), authUser.getEmail(), authUser.getName(), UserRole.valueOf(authUser.getAuthorities().iterator().next().getAuthority()));
    }
}
