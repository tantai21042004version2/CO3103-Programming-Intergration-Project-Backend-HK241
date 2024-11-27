package L03.CNPM.Music.responses.playlist;

import L03.CNPM.Music.models.Playlist;
import L03.CNPM.Music.models.User;
import L03.CNPM.Music.responses.users.UserResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlaylistResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("is_public")
    private Boolean isPublic;

    @JsonProperty("user")
    private UserResponse user;

    public static PlaylistResponse fromPlaylist(Playlist playlist, User user) {
        return PlaylistResponse.builder()
                .id(playlist.getId())
                .name(playlist.getName())
                .isPublic(playlist.getIsPublic())
                .user(UserResponse.fromUser(user))
                .build();
    }
}