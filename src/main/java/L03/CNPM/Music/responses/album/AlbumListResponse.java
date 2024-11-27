package L03.CNPM.Music.responses.album;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class AlbumListResponse {
    private List<AlbumResponse> albumResponseList;
    private int totalPages;
}
