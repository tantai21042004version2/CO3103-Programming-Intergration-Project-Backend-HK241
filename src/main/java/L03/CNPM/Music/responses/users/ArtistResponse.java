package L03.CNPM.Music.responses.users;

import L03.CNPM.Music.models.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArtistResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("username")
    private String username;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("artist_name")
    private String artistName;

    @JsonProperty("biography")
    private String biography;

    public static ArtistResponse fromUser(User user) {
        return ArtistResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .imageUrl(user.getImageUrl())
                .artistName(user.getArtistName())
                .biography(user.getBiography())
                .build();
    }
}
