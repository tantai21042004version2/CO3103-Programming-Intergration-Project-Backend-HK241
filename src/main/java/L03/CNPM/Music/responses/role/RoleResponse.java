package L03.CNPM.Music.responses.role;

import com.fasterxml.jackson.annotation.JsonProperty;
import L03.CNPM.Music.models.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoleResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    public static RoleResponse fromRole(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }
}
