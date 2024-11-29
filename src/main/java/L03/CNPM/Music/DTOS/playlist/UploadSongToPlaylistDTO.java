package L03.CNPM.Music.DTOS.playlist;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadSongToPlaylistDTO {
    @JsonProperty("add_song_ids")
    private List<Long> addSongIds;

    @JsonProperty("remove_song_ids")
    private List<Long> removeSongIds;
}
