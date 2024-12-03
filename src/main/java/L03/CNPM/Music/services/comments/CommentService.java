package L03.CNPM.Music.services.comments;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import L03.CNPM.Music.repositories.CommentRepository;
import L03.CNPM.Music.repositories.UserRepository;
import L03.CNPM.Music.repositories.SongRepository;

import L03.CNPM.Music.DTOS.comment.CreateCommentDTO;
import L03.CNPM.Music.models.Comment;
import L03.CNPM.Music.models.User;
import L03.CNPM.Music.models.Song;
import L03.CNPM.Music.exceptions.DataNotFoundException;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import L03.CNPM.Music.DTOS.comment.UpdateCommentDTO;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final SongRepository songRepository;

    @Override
    public Page<Comment> Get(Long songId, Pageable pageable) {
        if (songId == null) {
            return commentRepository.findAll(pageable);
        }
        return commentRepository.findAll(songId, pageable);
    }

    @Override
    public Comment Create(CreateCommentDTO createCommentDTO, String userId) throws Exception {
        CompletableFuture<User> userFuture = CompletableFuture
                .supplyAsync(() -> {
                    try {
                        return userRepository.findById(Long.parseLong(userId))
                                .orElseThrow(() -> new DataNotFoundException("User not found"));
                    } catch (DataNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });

        CompletableFuture<Song> songFuture = CompletableFuture
                .supplyAsync(() -> {
                    try {
                        return songRepository.findById(createCommentDTO.getSongId())
                                .orElseThrow(() -> new DataNotFoundException("Song not found"));
                    } catch (DataNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });

        User user = userFuture.join();
        Song song = songFuture.join();

        Comment comment = Comment.builder()
                .userId(user.getId())
                .songId(song.getId())
                .content(createCommentDTO.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return commentRepository.save(comment);
    }

    @Override
    public Comment Update(Long commentId, UpdateCommentDTO updateCommentDTO, String userId) throws Exception {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new DataNotFoundException("Comment not found"));

        if (!comment.getUserId().equals(Long.parseLong(userId))) {
            throw new DataNotFoundException("You are not allowed to update this comment");
        }

        comment.setContent(updateCommentDTO.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    @Override
    public Comment Delete(Long commentId, String userId) throws Exception {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new DataNotFoundException("Comment not found"));

        if (!comment.getUserId().equals(Long.parseLong(userId))) {
            throw new DataNotFoundException("You are not allowed to delete this comment");
        }

        comment.setDeletedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }
}
