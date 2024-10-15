package jpabook.trello_project.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawRequest {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String password;
}
