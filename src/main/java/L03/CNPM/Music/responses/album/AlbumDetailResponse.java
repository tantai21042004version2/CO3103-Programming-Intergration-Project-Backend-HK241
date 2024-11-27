package L03.CNPM.Music.responses.album;

import L03.CNPM.Music.models.Album;
import L03.CNPM.Music.models.Song;
import L03.CNPM.Music.models.User;
import L03.CNPM.Music.responses.song.SongResponse;
import L03.CNPM.Music.responses.users.ArtistResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlbumDetailResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("artist")
    private ArtistResponse artist;

    @JsonProperty("songs")
    private List<SongResponse> songs;

    @JsonProperty("description")
    private String description;

    @JsonProperty("cover_image_url")
    private String coverImageUrl;

    @JsonProperty("release_date")
    private String releaseDate;

    @JsonProperty("status")
    private Album.Status status;

    @JsonProperty("create_at")
    private String createdAt;

    public static AlbumDetailResponse fromAlbum(Album album, List<Song> songs, User artist) {
        return AlbumDetailResponse.builder()
                .id(album.getId())
                .name(album.getName())
                .description(album.getDescription())
                .coverImageUrl(album.getCoverUrl())
                .releaseDate(album.getReleaseDate())
                .status(album.getStatus())
                .createdAt(album.getCreatedAt())
                .artist(ArtistResponse.fromUser(artist))
                .songs(songs.stream().map(SongResponse::fromSong).collect(Collectors.toList()))
                .build();
    }
}
