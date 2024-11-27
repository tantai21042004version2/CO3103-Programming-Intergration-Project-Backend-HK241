package L03.CNPM.Music.responses.album;

import L03.CNPM.Music.models.Album;
import L03.CNPM.Music.responses.song.SongResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModifyAlbumResponse {
    private Long id;
    private String name;
    private String description;
    private List<SongResponse> song;

    public ModifyAlbumResponse(Album album, List<SongResponse> song) {
        this.song = song;
        this.description = album.getDescription();
        this.id = album.getId();
        this.name = album.getName();
    }
}
