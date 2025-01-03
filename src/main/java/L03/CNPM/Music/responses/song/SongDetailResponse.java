package L03.CNPM.Music.responses.song;

import com.fasterxml.jackson.annotation.JsonProperty;

import L03.CNPM.Music.models.Song;
import L03.CNPM.Music.models.User;
import L03.CNPM.Music.responses.album.AlbumResponse;
import L03.CNPM.Music.responses.users.ArtistResponse;
import L03.CNPM.Music.responses.genre.GenreResponse;
import L03.CNPM.Music.models.Album;
import L03.CNPM.Music.models.Genre;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

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

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("album")
    private AlbumResponse album;

    @JsonProperty("genre")
    private GenreResponse genre;

    @JsonProperty("description")
    private String description;

    @JsonProperty("artist")
    private ArtistResponse artist;

    @JsonProperty("release_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @JsonProperty("status")
    private Song.Status status;

    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public static SongDetailResponse fromSong(Song song, User artist, Album album, Genre genre) {
        if (artist == null) {
            artist = new User();
        }

        if (album == null) {
            album = new Album();
        }

        if (genre == null) {
            genre = new Genre();
        }

        return SongDetailResponse.builder()
                .id(song.getId().toString())
                .name(song.getName())
                .duration(song.getDuration())
                .secureUrl(song.getSecureUrl())
                .imageUrl(song.getImageUrl())
                .status(song.getStatus())
                .description(song.getDescription())
                .releaseDate(song.getReleaseDate())
                .createdAt(song.getCreatedAt())
                .updatedAt(song.getUpdatedAt())
                .album(AlbumResponse.fromAlbum(album, genre))
                .genre(GenreResponse.fromGenre(genre))
                .artist(ArtistResponse.fromUser(artist))
                .build();
    }
}
