package L03.CNPM.Music.responses.users;

import com.fasterxml.jackson.annotation.JsonProperty;

import L03.CNPM.Music.models.Role;
import L03.CNPM.Music.models.User;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDetailResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("country")
    private String country;

    @JsonProperty("date_of_birth")
    private String dateOfBirth;

    @JsonProperty("role")
    private Role role;

    @JsonProperty("active")
    private boolean active;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    public static UserDetailResponse fromUser(User user) {
        return UserDetailResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .country(user.getCountry())
                .dateOfBirth(user.getDateOfBirth())
                .role(user.getRole())
                .active(user.isActive())
                .imageUrl(user.getPublicImageId())
                .createdAt(user.getCreatedAt().toString())
                .updatedAt(user.getUpdatedAt().toString())
                .build();
    }
}
