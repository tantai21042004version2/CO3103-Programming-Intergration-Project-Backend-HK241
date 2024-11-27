package L03.CNPM.Music.responses.users;

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
public class UserListResponse {
    @JsonProperty("users")
    private List<UserResponse> users;

    @JsonProperty("total_pages")
    private int totalPages;

    @JsonProperty("current_page")
    private int currentPage;

    @JsonProperty("items_per_page")
    private int itemsPerPage;
}
