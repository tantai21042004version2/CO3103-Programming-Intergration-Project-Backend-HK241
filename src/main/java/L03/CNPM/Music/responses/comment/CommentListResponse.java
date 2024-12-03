package L03.CNPM.Music.responses.comment;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentListResponse {
    @JsonProperty("comments")
    private List<CommentResponse> comments;

    @JsonProperty("current_page")
    private int currentPage;

    @JsonProperty("total_pages")
    private int totalPages;

    @JsonProperty("items_per_page")
    private int itemsPerPage;

}
