package L03.CNPM.Music.responses.album;

import L03.CNPM.Music.models.Album;
import L03.CNPM.Music.models.Genre;
import L03.CNPM.Music.responses.genre.GenreResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlbumResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("cover_image_url")
    private String coverImageUrl;

    @JsonProperty("release_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @JsonProperty("genre")
    private GenreResponse genreResponse;

    @JsonProperty("status")
    private Album.Status status;

    public static AlbumResponse fromAlbum(Album album, Genre genre) {
        return AlbumResponse.builder()
                .id(album.getId())
                .name(album.getName())
                .description(album.getDescription())
                .coverImageUrl(album.getCoverUrl())
                .releaseDate(album.getReleaseDate())
                .status(album.getStatus())
                .genreResponse(GenreResponse.fromGenre(genre))
                .build();
    }
}