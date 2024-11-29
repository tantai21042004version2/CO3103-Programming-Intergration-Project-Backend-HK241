package L03.CNPM.Music.responses.album;

import L03.CNPM.Music.models.Album;
import L03.CNPM.Music.models.Genre;
import L03.CNPM.Music.models.Song;
import L03.CNPM.Music.models.User;
import L03.CNPM.Music.responses.song.SongResponse;
import L03.CNPM.Music.responses.users.ArtistResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import L03.CNPM.Music.responses.genre.GenreResponse;

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

    @JsonProperty("genre")
    private GenreResponse genre;

    @JsonProperty("songs")
    private List<SongResponse> songs;

    @JsonProperty("description")
    private String description;

    @JsonProperty("cover_image_url")
    private String coverImageUrl;

    @JsonProperty("release_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @JsonProperty("status")
    private Album.Status status;

    @JsonProperty("create_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public static AlbumDetailResponse fromAlbum(Album album, List<Song> songs, User artist, Genre genre) {
        return AlbumDetailResponse.builder()
                .id(album.getId())
                .name(album.getName())
                .description(album.getDescription())
                .coverImageUrl(album.getCoverUrl())
                .releaseDate(album.getReleaseDate())
                .status(album.getStatus())
                .artist(ArtistResponse.fromUser(artist))
                .genre(GenreResponse.fromGenre(genre))
                .songs(songs.stream().map(SongResponse::fromSong).collect(Collectors.toList()))
                .createdAt(album.getCreatedAt())
                .updatedAt(album.getUpdatedAt())
                .build();
    }
}
