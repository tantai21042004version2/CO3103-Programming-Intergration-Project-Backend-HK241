package L03.CNPM.Music.responses.playlist;

import com.fasterxml.jackson.annotation.JsonProperty;

import L03.CNPM.Music.models.Playlist;
import L03.CNPM.Music.models.User;
import L03.CNPM.Music.models.Genre;
import L03.CNPM.Music.responses.users.UserResponse;
import L03.CNPM.Music.responses.genre.GenreResponse;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlaylistDetailResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("genre")
    private GenreResponse genre;

    @JsonProperty("cover_url")
    private String coverUrl;

    @JsonProperty("is_public")
    private Boolean isPublic;

    @JsonProperty("status")
    private String status;

    @JsonProperty("user")
    private UserResponse user;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    public static PlaylistDetailResponse fromPlaylist(Playlist playlist, User user, Genre genre) {
        return PlaylistDetailResponse.builder()
                .id(playlist.getId())
                .name(playlist.getName())
                .description(playlist.getDescription())
                .genre(GenreResponse.fromGenre(genre))
                .coverUrl(playlist.getCoverUrl())
                .isPublic(playlist.getIsPublic())
                .status(playlist.getStatus().name())
                .user(UserResponse.fromUser(user))
                .createdAt(playlist.getCreatedAt())
                .updatedAt(playlist.getUpdatedAt())
                .build();
    }
}
