package L03.CNPM.Music.responses.users;

import L03.CNPM.Music.models.Role;
import L03.CNPM.Music.models.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("username")
    private String username;

    @JsonProperty("role")
    private Role role;

    @JsonProperty("image_url")
    private String imageUrl;

    public static UserResponse fromUser(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .imageUrl(user.getImageUrl())
                .build();
    }
}
