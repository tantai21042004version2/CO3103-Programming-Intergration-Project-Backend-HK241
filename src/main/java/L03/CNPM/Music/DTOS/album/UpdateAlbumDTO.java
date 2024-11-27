package L03.CNPM.Music.DTOS.album;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateAlbumDTO {
    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("list_delete")
    private List<Long> deleteList;

    @JsonProperty("list_add")
    private List<Long> addList;

}
