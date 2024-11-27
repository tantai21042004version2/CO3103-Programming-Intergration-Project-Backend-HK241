package L03.CNPM.Music.responses.song;

import com.fasterxml.jackson.annotation.JsonProperty;

import L03.CNPM.Music.models.Song;
import L03.CNPM.Music.models.Album;
import L03.CNPM.Music.models.User;
import L03.CNPM.Music.responses.album.AlbumResponse;
import L03.CNPM.Music.responses.users.ArtistResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SongDetailResponse {
    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("duration")
    private Double duration;

    @JsonProperty("secure_url")
    private String secureUrl;

    @JsonProperty("description")
    private String description;

    @JsonProperty("artist")
    private ArtistResponse artist;

    @JsonProperty("release_date")
    private String releaseDate;

    @JsonProperty("status")
    private Song.Status status;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    public static SongDetailResponse fromSong(Song song, User artist) {
        if (artist == null) {
            artist = new User();
        }

        return SongDetailResponse.builder()
                .id(song.getId().toString())
                .name(song.getName())
                .duration(song.getDuration())
                .secureUrl(song.getSecureUrl())
                .status(song.getStatus())
                .description(song.getDescription())
                .releaseDate(song.getReleaseDate())
                .createdAt(song.getCreatedAt())
                .updatedAt(song.getUpdatedAt())
                .artist(ArtistResponse.fromUser(artist))
                .build();
    }
}
