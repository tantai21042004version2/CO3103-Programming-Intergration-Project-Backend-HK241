package L03.CNPM.Music.responses.song;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CloudinaryResponse {
    @JsonProperty("secure_url")
    private String secureUrl;

    @JsonProperty("public_id")
    private String publicId;

    @JsonProperty("duration")
    private Double duration;

    public static CloudinaryResponse fromMap(Map<String, Object> map) {
        return CloudinaryResponse.builder()
                .secureUrl((String) map.get("secure_url"))
                .publicId((String) map.get("public_id"))
                .duration((Double) map.get("duration"))
                .build();
    }
}