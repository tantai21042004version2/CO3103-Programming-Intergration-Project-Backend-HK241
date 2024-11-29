package L03.CNPM.Music.responses.playlist;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import L03.CNPM.Music.models.Playlist;
import L03.CNPM.Music.models.Song;
import L03.CNPM.Music.models.User;
import L03.CNPM.Music.models.Genre;
import L03.CNPM.Music.responses.users.UserResponse;
import L03.CNPM.Music.responses.genre.GenreResponse;
import L03.CNPM.Music.responses.song.SongResponse;
import lombok.*;
import java.util.stream.Collectors;

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

    @JsonProperty("songs")
    private List<SongResponse> songs;

    @JsonProperty("user")
    private UserResponse user;

    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public static PlaylistDetailResponse fromPlaylist(
            Playlist playlist, User user, Genre genre, List<Song> songs) {
        return PlaylistDetailResponse.builder()
                .id(playlist.getId())
                .name(playlist.getName())
                .description(playlist.getDescription())
                .genre(GenreResponse.fromGenre(genre))
                .coverUrl(playlist.getCoverUrl())
                .isPublic(playlist.getIsPublic())
                .status(playlist.getStatus())
                .songs(songs.stream().map(SongResponse::fromSong).collect(Collectors.toList()))
                .user(UserResponse.fromUser(user))
                .createdAt(playlist.getCreatedAt())
                .updatedAt(playlist.getUpdatedAt())
                .build();
    }
}
