package L03.CNPM.Music.DTOS.song;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SongMetadataDTO {
    @NotBlank(message = "Song name is required")
    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @NotBlank(message = "Release date is required")
    @JsonProperty("release_date")
    private String releaseDate;

    @JsonProperty("artist_id")
    private Long artistId;

    @NotNull(message = "Genre is required")
    @JsonProperty("genre_id")
    private Long genreId;

    @NotNull(message = "Duration is required")
    @JsonProperty("duration")
    private Double duration;

    @JsonProperty("public_id")
    private String publicId;

    @NotBlank(message = "Secure URL is required")
    @JsonProperty("secure_url")
    private String secureUrl;
}
