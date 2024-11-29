package L03.CNPM.Music.DTOS.album;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApproveAlbumDTO {
    @NotNull
    @JsonProperty("is_approved")
    private Boolean isApproved;

    @NotNull
    @JsonProperty("description")
    private String description;
}
