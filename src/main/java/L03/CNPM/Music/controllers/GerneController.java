package L03.CNPM.Music.controllers;

import L03.CNPM.Music.models.Genre;
import L03.CNPM.Music.responses.ResponseObject;
import L03.CNPM.Music.responses.genre.GenreResponse;
import L03.CNPM.Music.services.gerne.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("${api.prefix}/genres")
@RequiredArgsConstructor
public class GerneController {
    private final GenreService genreService;

    @GetMapping("")
    public ResponseEntity<ResponseObject> Get(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "limit", defaultValue = "10") int limit,
            @RequestParam(name = "keyword", defaultValue = "") String keyword) {

        try {
            Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("id").ascending());

            List<Genre> genres = genreService.Get(pageable, keyword);

            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message("Get all genres successfully")
                    .data(genres.stream().map(GenreResponse::fromGenre).toList())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message(e.getMessage())
                            .build());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<ResponseObject> GetAllGenres() {
        try {
            List<Genre> genres = genreService.GetAll();
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message("Get all genres successfully")
                    .data(genres.stream().map(GenreResponse::fromGenre).toList())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message(e.getMessage())
                            .build());
        }
    }
}
