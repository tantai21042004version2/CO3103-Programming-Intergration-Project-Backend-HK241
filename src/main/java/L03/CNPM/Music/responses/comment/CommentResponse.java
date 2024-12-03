package L03.CNPM.Music.responses.comment;

import L03.CNPM.Music.responses.users.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import L03.CNPM.Music.models.Comment;
import L03.CNPM.Music.models.User;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("content")
    private String content;

    @JsonProperty("user")
    private UserResponse user;

    public static CommentResponse fromComment(Comment comment, User user) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .user(UserResponse.fromUser(user))
                .build();
    }
}
