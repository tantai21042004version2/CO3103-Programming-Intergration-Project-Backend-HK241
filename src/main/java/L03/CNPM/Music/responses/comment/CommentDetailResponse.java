package L03.CNPM.Music.responses.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import L03.CNPM.Music.responses.song.SongResponse;
import L03.CNPM.Music.responses.users.UserResponse;
import L03.CNPM.Music.models.Comment;
import L03.CNPM.Music.models.User;
import L03.CNPM.Music.models.Song;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDetailResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("content")
    private String content;

    @JsonProperty("user")
    private UserResponse user;

    @JsonProperty("song")
    private SongResponse song;

    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @JsonProperty("deleted_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deletedAt;

    public static CommentDetailResponse fromComment(Comment comment, User user, Song song) {
        return CommentDetailResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .user(UserResponse.fromUser(user))
                .song(SongResponse.fromSong(song))
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
