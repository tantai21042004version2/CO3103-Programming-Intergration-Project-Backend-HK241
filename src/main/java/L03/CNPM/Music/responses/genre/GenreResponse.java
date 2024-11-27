package L03.CNPM.Music.responses.genre;

import L03.CNPM.Music.models.Genre;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenreResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    public static GenreResponse fromGenre(Genre genre) {
        return GenreResponse.builder()
                .id(genre.getId())
                .name(genre.getName())
                .build();
    }
}
