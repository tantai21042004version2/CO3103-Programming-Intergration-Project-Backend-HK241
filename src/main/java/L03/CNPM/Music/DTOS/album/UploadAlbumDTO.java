package L03.CNPM.Music.DTOS.album;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UploadAlbumDTO {
    @JsonProperty("name")
    @NotBlank(message = "Name is required")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("cover_url")
    @NotBlank(message = "Cover URL is required")
    private String coverUrl;

    @JsonProperty("release_date")
    @NotBlank(message = "Release date is required")
    private String releaseDate;

    @JsonProperty("genre_id")
    @NotNull(message = "Genre ID is required")
    private Long genreId;
}
