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
public class CreateUserDTO {
    @JsonProperty("email")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @JsonProperty("username")
    @NotBlank(message = "Username cannot be blank")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    private String password;

    @JsonProperty("retype_password")
    private String retypePassword;

    @JsonProperty("country")
    private String country;

    @JsonProperty("date_of_birth")
    private String dateOfBirth;
}
