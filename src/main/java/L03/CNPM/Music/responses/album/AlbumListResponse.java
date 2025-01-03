package L03.CNPM.Music.responses.album;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class AlbumListResponse {
    @JsonProperty("albums")
    private List<AlbumResponse> albumResponseList;

    @JsonProperty("total_pages")
    private int totalPages;

    @JsonProperty("current_page")
    private int currentPage;

    @JsonProperty("items_per_page")
    private int itemsPerPage;
}
