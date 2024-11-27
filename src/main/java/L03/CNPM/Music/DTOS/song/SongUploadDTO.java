package L03.CNPM.Music.DTOS.song;

import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SongUploadDTO {
    @NotNull(message = "Metadata is required")
    @JsonProperty("metadata")
    private SongMetadataDTO metadata;

    @NotNull(message = "Song file is required")
    @JsonProperty("file")
    private MultipartFile file;
}
