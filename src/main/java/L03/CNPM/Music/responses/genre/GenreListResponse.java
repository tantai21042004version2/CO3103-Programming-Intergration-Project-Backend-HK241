package L03.CNPM.Music.responses.genre;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class GenreListResponse {
    @JsonProperty("genres")
    private List<GenreResponse> genres;

    @JsonProperty("total_pages")
    private int totalPages;

    @JsonProperty("current_page")
    private int currentPage;

    @JsonProperty("items_per_page")
    private int itemsPerPage;
}
