package L03.CNPM.Music.responses.song;

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
public class SongListResponse {
    @JsonProperty("songs")
    private List<SongResponse> songs;

    @JsonProperty("current_page")
    private int currentPage;

    @JsonProperty("total_pages")
    private int totalPages;

    @JsonProperty("items_per_page")
    private int itemsPerPage;
}
