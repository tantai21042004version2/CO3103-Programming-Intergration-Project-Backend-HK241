package L03.CNPM.Music.DTOS.album;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UploadSongToAlbumDTO {
    @JsonProperty("add_song_ids")
    private List<Long> addSongIds;

    @JsonProperty("remove_song_ids")
    private List<Long> removeSongIds;
}
