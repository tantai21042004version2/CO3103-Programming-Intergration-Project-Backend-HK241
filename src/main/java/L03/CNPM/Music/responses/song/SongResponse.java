package L03.CNPM.Music.responses.song;

import com.fasterxml.jackson.annotation.JsonProperty;

import L03.CNPM.Music.models.Song;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SongResponse {
    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("secure_url")
    private String secureUrl;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("status")
    private String status;

    public static SongResponse fromSong(Song song) {
        return SongResponse.builder()
                .id(song.getId().toString())
                .name(song.getName())
                .secureUrl(song.getSecureUrl())
                .imageUrl(song.getImageUrl())
                .status(song.getStatus().name())
                .build();
    }
}
