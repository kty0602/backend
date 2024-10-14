package jpabook.trello_project.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String name;
    @Pattern(message = "비밀번호는 최소 8글자 이상, 영문 + 숫자 + 특수문자를 최소 1글자씩 포함해야 합니다.",
            regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$")
    private String password;
    @NotBlank
    private String userRole;
}