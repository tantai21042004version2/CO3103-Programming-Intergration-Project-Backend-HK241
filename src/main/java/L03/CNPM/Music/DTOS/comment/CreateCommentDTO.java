package L03.CNPM.Music.DTOS.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCommentDTO {
    @NotBlank(message = "Content is required")
    @JsonProperty("content")
    private String content;
    @NotNull(message = "Song ID is required")
    @JsonProperty("song_id")
    private Long songId;
}
