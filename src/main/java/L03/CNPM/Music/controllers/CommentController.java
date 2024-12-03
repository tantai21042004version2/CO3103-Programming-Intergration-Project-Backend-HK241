package L03.CNPM.Music.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import L03.CNPM.Music.DTOS.comment.CreateCommentDTO;
import L03.CNPM.Music.DTOS.comment.UpdateCommentDTO;
import L03.CNPM.Music.components.JwtTokenUtils;
import L03.CNPM.Music.exceptions.DataNotFoundException;
import L03.CNPM.Music.responses.ResponseObject;
import L03.CNPM.Music.responses.comment.CommentDetailResponse;
import lombok.RequiredArgsConstructor;

import L03.CNPM.Music.services.comments.ICommentService;
import L03.CNPM.Music.services.users.IUserService;
import L03.CNPM.Music.services.song.ISongService;
import jakarta.validation.Valid;

import L03.CNPM.Music.models.Comment;
import L03.CNPM.Music.models.User;
import L03.CNPM.Music.models.Song;
import L03.CNPM.Music.responses.comment.CommentResponse;
import L03.CNPM.Music.responses.comment.CommentListResponse;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("${api.prefix}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final ICommentService commentService;
    private final IUserService userService;
    private final ISongService songService;
    private final JwtTokenUtils jwtTokenUtils;

    @GetMapping("/list/{song_id}")
    public ResponseEntity<ResponseObject> Get(
            @PathVariable("song_id") Long songId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        if (page < 1) {
            page = 1;
        }

        PageRequest pageRequest = PageRequest.of(
                page - 1, limit,
                Sort.by("id").ascending());

        Page<Comment> commentPage = commentService.Get(songId, pageRequest);

        try {
            List<Long> userIds = commentPage.getContent().stream()
                    .map(Comment::getUserId)
                    .distinct()
                    .collect(Collectors.toList());

            Map<Long, User> userMap = userService.GetByIDs(userIds).stream()
                    .collect(Collectors.toMap(User::getId, user -> user));

            List<CommentResponse> commentResponses = commentPage.getContent().stream()
                    .map(comment -> {
                        User user = userMap.get(comment.getUserId());
                        return CommentResponse.fromComment(comment, user);
                    })
                    .collect(Collectors.toList());

            int totalPages = commentPage.getTotalPages();
            int currentPage = commentPage.getNumber() + 1;
            int itemsPerPage = commentPage.getSize();

            CommentListResponse commentListResponse = CommentListResponse.builder()
                    .comments(commentResponses)
                    .totalPages(totalPages)
                    .currentPage(currentPage)
                    .itemsPerPage(itemsPerPage)
                    .build();

            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Get all comments successfully")
                    .status(HttpStatus.OK)
                    .data(commentListResponse)
                    .build());
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder()
                    .message("Internal server error: " + e.getMessage())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .data(null)
                    .build());
        }
    }

    @PostMapping("")
    public ResponseEntity<ResponseObject> Create(
            @RequestBody @Valid CreateCommentDTO createCommentDTO,
            @RequestHeader("Authorization") String authorizationHeader,
            BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message("Validation errors: " + errorMessages)
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .build());
        }

        try {
            String token = authorizationHeader.substring(7);
            String userId = jwtTokenUtils.getUserId(token);

            Comment comment = commentService.Create(createCommentDTO, userId);

            CompletableFuture<User> userFuture = CompletableFuture
                    .supplyAsync(() -> {
                        try {
                            return userService.Detail(comment.getUserId());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
            CompletableFuture<Song> songFuture = CompletableFuture
                    .supplyAsync(() -> {
                        try {
                            return songService.Detail(comment.getSongId());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

            User user = userFuture.join();
            Song song = songFuture.join();

            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Comment created successfully")
                    .status(HttpStatus.OK)
                    .data(CommentDetailResponse.fromComment(comment, user, song))
                    .build());
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message("Data not found: " + e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder()
                    .message("Internal server error: " + e.getMessage())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .data(null)
                    .build());
        }
    }

    @PutMapping("/{comment_id}")
    public ResponseEntity<ResponseObject> Update(
            @PathVariable("comment_id") Long commentId,
            @RequestBody @Valid UpdateCommentDTO updateCommentDTO,
            @RequestHeader("Authorization") String authorizationHeader,
            BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message("Validation errors: " + errorMessages)
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .build());
        }

        try {
            String token = authorizationHeader.substring(7);
            String userId = jwtTokenUtils.getUserId(token);

            Comment comment = commentService.Update(commentId, updateCommentDTO, userId);

            CompletableFuture<User> userFuture = CompletableFuture
                    .supplyAsync(() -> {
                        try {
                            return userService.Detail(comment.getUserId());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

            CompletableFuture<Song> songFuture = CompletableFuture
                    .supplyAsync(() -> {
                        try {
                            return songService.Detail(comment.getSongId());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

            User user = userFuture.join();
            Song song = songFuture.join();

            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Comment updated successfully")
                    .status(HttpStatus.OK)
                    .data(CommentDetailResponse.fromComment(comment, user, song))
                    .build());
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message("Data not found: " + e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder()
                    .message("Internal server error: " + e.getMessage())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .data(null)
                    .build());
        }
    }

    @DeleteMapping("/{comment_id}")
    public ResponseEntity<ResponseObject> Delete(
            @PathVariable("comment_id") Long commentId,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = authorizationHeader.substring(7);
            String userId = jwtTokenUtils.getUserId(token);

            Comment comment = commentService.Delete(commentId, userId);

            CompletableFuture<User> userFuture = CompletableFuture
                    .supplyAsync(() -> {
                        try {
                            return userService.Detail(comment.getUserId());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

            CompletableFuture<Song> songFuture = CompletableFuture
                    .supplyAsync(() -> {
                        try {
                            return songService.Detail(comment.getSongId());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

            User user = userFuture.join();
            Song song = songFuture.join();

            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Comment deleted successfully")
                    .status(HttpStatus.OK)
                    .data(CommentDetailResponse.fromComment(comment, user, song))
                    .build());
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message("Data not found: " + e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder()
                    .message("Internal server error: " + e.getMessage())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .data(null)
                    .build());
        }
    }
}
