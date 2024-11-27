package L03.CNPM.Music.DTOS.song;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SongMetadataDTO {
    @NotBlank(message = "Song name is required")
    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("release_date")
    private String releaseDate;

    @JsonProperty("artist_id")
    private Long artistId;

    @JsonProperty("album_id")
    private Long albumId;

    @JsonProperty("duration")
    private Double duration;

    @JsonProperty("cloudinary_version")
    private Long cloudinaryVersion;

    @JsonProperty("public_id")
    private String publicId;

    @JsonProperty("secure_url")
    private String secureUrl;
}
