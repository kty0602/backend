package jpabook.trello_project.domain.user.entity;

import jakarta.persistence.*;
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

    public User(String email, String name, String password, UserRole userRole) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.userRole = userRole;
    }
}
