package L03.CNPM.Music.DTOS.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@Getter
@Setter
public class UserLoginDTO {
    @JsonProperty("email")
    private String email;

    @JsonProperty("username")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    private String password;

    @JsonProperty("role_id")
    private Long roleId;

    public boolean isPasswordBlank() {
        return password == null || password.trim().isEmpty();
    }
}
