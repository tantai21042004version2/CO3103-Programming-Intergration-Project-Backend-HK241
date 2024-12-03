package L03.CNPM.Music.DTOS.comment;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCommentDTO {
    @NotBlank(message = "Content is required")
    private String content;
}
