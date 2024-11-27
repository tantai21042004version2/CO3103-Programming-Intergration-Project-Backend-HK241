package L03.CNPM.Music.DTOS.playlist;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePlaylistDTO {
    @JsonProperty("description")
    private String description;

    @JsonProperty("genre_id")
    private Long genreId;

    @JsonProperty("is_public")
    private Boolean isPublic;

    @JsonProperty("add_song_ids")
    private List<Long> addSongIds;

    @JsonProperty("remove_song_ids")
    private List<Long> removeSongIds;
}
