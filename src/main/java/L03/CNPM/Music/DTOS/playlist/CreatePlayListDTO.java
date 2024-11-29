package L03.CNPM.Music.DTOS.playlist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class CreatePlayListDTO {
    @JsonProperty("name")
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("genre_id")
    private Long genreId;

    @JsonProperty("cover_url")
    private String coverUrl;

    @JsonProperty("is_public")
    private Boolean isPublic;
}
