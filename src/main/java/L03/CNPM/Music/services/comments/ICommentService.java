package L03.CNPM.Music.services.comments;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import L03.CNPM.Music.DTOS.comment.CreateCommentDTO;
import L03.CNPM.Music.DTOS.comment.UpdateCommentDTO;
import L03.CNPM.Music.models.Comment;

public interface ICommentService {
    Page<Comment> Get(Long songId, Pageable pageable);

    Comment Create(CreateCommentDTO createCommentDTO, String userId) throws Exception;

    Comment Update(Long commentId, UpdateCommentDTO updateCommentDTO, String userId) throws Exception;

    Comment Delete(Long commentId, String userId) throws Exception;
}
