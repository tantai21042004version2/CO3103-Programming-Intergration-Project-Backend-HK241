package L03.CNPM.Music.controllers;

import L03.CNPM.Music.services.song.ISongService;
import L03.CNPM.Music.services.users.IUserService;
import L03.CNPM.Music.utils.DateUtils;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import L03.CNPM.Music.DTOS.song.SongMetadataDTO;
import L03.CNPM.Music.components.JwtTokenUtils;
import L03.CNPM.Music.models.Song;
import L03.CNPM.Music.responses.ResponseObject;
import L03.CNPM.Music.responses.song.CloudinaryResponse;
import L03.CNPM.Music.responses.song.SongDetailResponse;
import L03.CNPM.Music.responses.song.SongListResponse;
import L03.CNPM.Music.responses.song.SongResponse;
import jakarta.validation.Valid;

import L03.CNPM.Music.models.User;
import L03.CNPM.Music.models.Album;
import L03.CNPM.Music.models.Genre;
import L03.CNPM.Music.services.album.IAlbumService;
import L03.CNPM.Music.services.gerne.IGenreService;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/songs")
@RequiredArgsConstructor
public class SongController {
        private final ISongService songService;
        private final JwtTokenUtils jwtTokenUtils;
        private final DateUtils dateUtils;
        private final IUserService userService;
        private final IAlbumService albumService;
        private final IGenreService genreService;

        // ENDPOINT: {{API_PREFIX}}/songs [GET]
        // GET ALL SONGS IN SYSTEM, USE MAINLY FOR ALL SYSTEM
        // HEADERS: AUTHENTICATION: YES (ALL USER CAN ACCESS)
        // PARAMS:
        // keyword: String, default value is ""
        // page: int, default value is 1
        // limit: int, default value is 10
        /*
         * RESPONSE:
         * {
         * "message": "Get all song successfully",
         * "status": "200 OK",
         * "data": {
         * "songs": [
         * id, name, duration, secureUrl, releaseDate, status, createdAt, updatedAt
         * ],
         * "totalPages": 10
         * }
         * }
         */
        @GetMapping("")
        public ResponseEntity<ResponseObject> Get(
                        @RequestParam(defaultValue = "", required = false) String keyword,
                        @RequestParam(defaultValue = "1") int page,
                        @RequestParam(defaultValue = "10") int limit) {
                if (page < 1) {
                        page = 1;
                }

                PageRequest pageRequest = PageRequest.of(
                                page - 1, limit,
                                Sort.by("id").ascending());

                Page<SongResponse> songPage = songService.Get(keyword, pageRequest)
                                .map(SongResponse::fromSong);

                int totalPages = songPage.getTotalPages();

                int currentPage = songPage.getNumber() + 1;

                int itemsPerPage = songPage.getSize();

                List<SongResponse> songResponses = songPage.getContent();
                SongListResponse songListResponse = SongListResponse.builder()
                                .songs(songResponses)
                                .totalPages(totalPages)
                                .currentPage(currentPage)
                                .itemsPerPage(itemsPerPage)
                                .build();

                return ResponseEntity.status(HttpStatus.OK).body(ResponseObject.builder()
                                .message("Get all song successfully")
                                .status(HttpStatus.OK)
                                .data(songListResponse)
                                .build());
        }

        @GetMapping("/detail/{id}")
        public ResponseEntity<ResponseObject> Detail(@PathVariable("id") Long id) {
                try {
                        Song song = songService.Detail(id);

                        User artist = null;
                        if (song.getArtistId() != null) {
                                artist = userService.Detail(song.getArtistId());
                        }
                        Album album = null;
                        if (song.getAlbumId() != null) {
                                album = albumService.Detail(song.getAlbumId());
                        }
                        Genre genre = null;
                        if (song.getGenreId() != null) {
                                genre = genreService.Detail(song.getGenreId());
                        }

                        return ResponseEntity.ok(ResponseObject.builder()
                                        .message("Get song detail successfully")
                                        .status(HttpStatus.OK)
                                        .data(SongDetailResponse.fromSong(song, artist, album, genre))
                                        .build());
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseObject.builder()
                                        .message(e.getMessage())
                                        .status(HttpStatus.BAD_REQUEST)
                                        .data(null)
                                        .build());
                }
        }

        // ENDPOINT: {{API_PREFIX}}/songs/artist [GET]
        // GET ALL SONGS OF AN ARTIST
        // HEADERS: AUTHENTICATION: ONLY ARTIST CAN ACCESS
        // PARAMS:
        // page: int, default value is 1
        // limit: int, default value is 10
        /*
         * RESPONSE:
         * {
         * "message": "Get artist song successfully",
         * "status": "200 OK",
         * "data": {
         * "songs": [
         * id, name, duration, secureUrl, releaseDate, status, createdAt, updatedAt
         * ],
         * "totalPages": 10
         * }
         * }
         */
        @GetMapping("/artist")
        @PreAuthorize("hasRole('ROLE_ARTIST')")
        public ResponseEntity<ResponseObject> GetArtistSong(
                        @RequestParam(defaultValue = "1") int page,
                        @RequestParam(defaultValue = "10") int limit,
                        @RequestHeader("Authorization") String authorizationHeader) {
                String token = authorizationHeader.substring(7);
                String userId = jwtTokenUtils.getUserId(token);

                if (userId == null) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseObject.builder()
                                        .message("Unauthorized")
                                        .status(HttpStatus.UNAUTHORIZED)
                                        .data(null)
                                        .build());
                }

                PageRequest pageRequest = PageRequest.of(
                                page - 1, limit,
                                Sort.by("id").ascending());

                Page<SongResponse> songPage = songService.GetByArtirstId(userId, pageRequest)
                                .map(SongResponse::fromSong);

                int totalPages = songPage.getTotalPages();

                int currentPage = songPage.getNumber() + 1;

                int itemsPerPage = songPage.getSize();

                List<SongResponse> songResponses = songPage.getContent();
                SongListResponse songListResponse = SongListResponse.builder()
                                .songs(songResponses)
                                .totalPages(totalPages)
                                .currentPage(currentPage)
                                .itemsPerPage(itemsPerPage)
                                .build();

                return ResponseEntity.status(HttpStatus.OK).body(ResponseObject.builder()
                                .message("Get artist song successfully")
                                .status(HttpStatus.OK)
                                .data(songListResponse)
                                .build());
        }

        // ENDPOINT: {{API_PREFIX}}/songs/pending [GET]
        // GET ALL PENDING SONGS IN SYSTEM
        // HEADERS: AUTHENTICATION: YES (ONLY ADMIN CAN ACCESS)
        // PARAMS:
        // keyword: String, default value is ""
        // page: int, default value is 1
        // limit: int, default value is 10
        /*
         * RESPONSE:
         * {
         * "message": "Get pending song successfully",
         * "status": "200 OK",
         * "data": {
         * "songs": [
         * id, name, duration, secureUrl, releaseDate, status, createdAt, updatedAt
         * ],
         * "totalPages": 10
         * }
         * }
         */
        @GetMapping("/pending")
        @PreAuthorize("hasRole('ROLE_ADMIN')")
        public ResponseEntity<ResponseObject> GetPendingSong(
                        @RequestParam(defaultValue = "", required = false) String keyword,
                        @RequestParam(defaultValue = "1") int page,
                        @RequestParam(defaultValue = "10") int limit) {

                if (page < 1) {
                        page = 1;
                }

                PageRequest pageRequest = PageRequest.of(
                                page - 1, limit,
                                Sort.by("id").ascending());

                Page<SongResponse> songPage = songService.GetPending(keyword, pageRequest)
                                .map(SongResponse::fromSong);

                int totalPages = songPage.getTotalPages();

                int currentPage = songPage.getNumber() + 1;

                int itemsPerPage = songPage.getSize();

                List<SongResponse> songResponses = songPage.getContent();
                SongListResponse songListResponse = SongListResponse.builder()
                                .songs(songResponses)
                                .totalPages(totalPages)
                                .currentPage(currentPage)
                                .itemsPerPage(itemsPerPage)
                                .build();

                return ResponseEntity.status(HttpStatus.OK).body(ResponseObject.builder()
                                .message("Get pending song successfully")
                                .status(HttpStatus.OK)
                                .data(songListResponse)
                                .build());
        }

        // ENDPOINT: {{API_PREFIX}}/songs/cloudinary [POST]
        // UPLOAD SONG TO CLOUDINARY, USE BEFORE UPLOAD SONG TO DATABASE
        // HEADERS: AUTHENTICATION: YES (ONLY ARTIST CAN ACCESS)
        // PARAMS:
        // file: MultipartFile
        /*
         * RESPONSE:
         * {
         * "message": "Upload song successfully",
         * "status": "200 OK",
         * "data": {
         * "public_id": "...",
         * "secure_url": "..."
         * }
         * }
         */
        @PostMapping("/cloudinary")
        @PreAuthorize("hasRole('ROLE_ARTIST')")
        public ResponseEntity<ResponseObject> UploadSongToCloudinary(
                        @RequestPart MultipartFile file) throws Exception {
                try {
                        Map<String, Object> response = songService.UploadSong(file);

                        return ResponseEntity.status(HttpStatus.OK).body(ResponseObject.builder()
                                        .message("Upload song successfully")
                                        .status(HttpStatus.OK)
                                        .data(CloudinaryResponse.fromMap(response))
                                        .build());
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseObject.builder()
                                        .message(e.getMessage())
                                        .status(HttpStatus.BAD_REQUEST)
                                        .data(null)
                                        .build());
                }
        }

        // ENDPOINT: {{API_PREFIX}}/songs [POST]
        // UPLOAD SONG TO DATABASE
        // HEADERS: AUTHENTICATION: YES (ONLY ARTIST CAN ACCESS)
        // PARAMS:
        // metadataSongDTO: SongMetadataDTO
        /*
         * RESPONSE:
         * {
         * "message": "Create song successfully",
         * "status": "200 OK",
         * "data": {
         * id, name, duration, secureUrl, releaseDate, status, createdAt, updatedAt
         * }
         * }
         */
        @PostMapping("")
        @PreAuthorize("hasRole('ROLE_ARTIST')")
        public ResponseEntity<ResponseObject> UploadSong(
                        @Valid @RequestBody SongMetadataDTO metadataSongDTO,
                        @RequestHeader("Authorization") String authorizationHeader,
                        BindingResult result) {
                if (result.hasErrors()) {
                        List<String> errorMessages = result.getFieldErrors()
                                        .stream()
                                        .map(FieldError::getDefaultMessage)
                                        .toList();

                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseObject.builder()
                                        .message(errorMessages.toString())
                                        .status(HttpStatus.BAD_REQUEST)
                                        .data(null)
                                        .build());
                }

                try {
                        String token = authorizationHeader.substring(7);
                        String userId = jwtTokenUtils.getUserId(token);
                        if (metadataSongDTO.getArtistId() == null) {
                                metadataSongDTO.setArtistId(Long.parseLong(userId));
                        }

                        if (!dateUtils.isValidDate(metadataSongDTO.getReleaseDate())) {
                                throw new IllegalArgumentException(
                                                "Release date is invalid.");
                        }

                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseObject.builder()
                                        .message(e.getMessage())
                                        .status(HttpStatus.BAD_REQUEST)
                                        .data(null)
                                        .build());
                }

                try {
                        Song newSong = songService.Create(metadataSongDTO);

                        User artist = userService.Detail(newSong.getArtistId());

                        Album album = albumService.Detail(newSong.getAlbumId());
                        Genre genre = genreService.Detail(newSong.getGenreId());

                        return ResponseEntity.status(HttpStatus.OK).body(ResponseObject.builder()
                                        .message("Create song successfully")
                                        .status(HttpStatus.OK)
                                        .data(SongDetailResponse.fromSong(newSong, artist, album, genre))
                                        .build());
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseObject.builder()
                                        .message(e.getMessage())
                                        .status(HttpStatus.BAD_REQUEST)
                                        .data(null)
                                        .build());
                }
        }

        // ENDPOINT: {{API_PREFIX}}/songs/submit/{id} [PATCH]
        // SUBMIT SONG TO ADMIN, USE AFTER UPLOAD SONG TO CLOUDINARY
        // HEADERS: AUTHENTICATION: YES (ONLY ARTIST CAN ACCESS)
        // PARAMS:
        // id: String
        /*
         * RESPONSE:
         * {
         * "message": "Submit song successfully",
         * "status": "200 OK",
         * "data": {
         * id, name, duration, secureUrl, releaseDate, status, createdAt, updatedAt
         * }
         * }
         */
        @PatchMapping("/submit/{id}")
        @PreAuthorize("hasRole('ROLE_ARTIST')")
        public ResponseEntity<ResponseObject> UpdateSong(@PathVariable String id,
                        @RequestHeader("Authorization") String authorizationHeader) {
                String token = authorizationHeader.substring(7);
                String userId = jwtTokenUtils.getUserId(token);

                if (userId == null) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseObject.builder()
                                        .message("Unauthorized")
                                        .status(HttpStatus.UNAUTHORIZED)
                                        .data(null)
                                        .build());
                }

                try {
                        Song song = songService.Update(Long.parseLong(id), userId);

                        User artist = userService.Detail(song.getArtistId());
                        Album album = albumService.Detail(song.getAlbumId());
                        Genre genre = genreService.Detail(song.getGenreId());

                        return ResponseEntity.status(HttpStatus.OK).body(ResponseObject.builder()
                                        .message("Update song successfully")
                                        .status(HttpStatus.OK)
                                        .data(SongDetailResponse.fromSong(song, artist, album, genre))
                                        .build());
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseObject.builder()
                                        .message(e.getMessage())
                                        .status(HttpStatus.BAD_REQUEST)
                                        .data(null)
                                        .build());
                }
        }

        // ENDPOINT: {{API_PREFIX}}/songs/approve/{id} [PATCH]
        // APPROVE SONG TO PUBLISH
        // HEADERS: AUTHENTICATION: YES (ONLY ADMIN CAN ACCESS)
        // PARAMS:
        // id: String
        /*
         * RESPONSE:
         * {
         * "message": "Approve song successfully",
         * "status": "200 OK",
         * "data": {
         * id, name, duration, secureUrl, releaseDate, status, createdAt, updatedAt
         * }
         * }
         */
        @PatchMapping("/approve/{id}")
        @PreAuthorize("hasRole('ROLE_ADMIN')")
        public ResponseEntity<ResponseObject> ApproveSong(@PathVariable String id) {
                try {
                        Song song = songService.ApproveSong(id);

                        User artist = userService.Detail(song.getArtistId());
                        Album album = albumService.Detail(song.getAlbumId());
                        Genre genre = genreService.Detail(song.getGenreId());

                        return ResponseEntity.status(HttpStatus.OK).body(ResponseObject.builder()
                                        .message("Approve song successfully")
                                        .status(HttpStatus.OK)
                                        .data(SongDetailResponse.fromSong(song, artist, album, genre))
                                        .build());
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseObject.builder()
                                        .message(e.getMessage())
                                        .status(HttpStatus.BAD_REQUEST)
                                        .data(null)
                                        .build());
                }
        }

        // ENDPOINT: {{API_PREFIX}}/songs/reject/{id} [PATCH]
        // REJECT SONG
        // HEADERS: AUTHENTICATION: YES (ONLY ADMIN CAN ACCESS)
        // PARAMS:
        // id: String
        /*
         * RESPONSE:
         * {
         * "message": "Reject song successfully",
         * "status": "200 OK",
         * "data": {
         * id, name, duration, secureUrl, releaseDate, status, createdAt, updatedAt
         * }
         * }
         */
        @PatchMapping("/reject/{id}")
        @PreAuthorize("hasRole('ROLE_ADMIN')")
        public ResponseEntity<ResponseObject> RejectSong(@PathVariable String id) {
                try {
                        Song song = songService.RejectSong(id);

                        User artist = userService.Detail(song.getArtistId());
                        Album album = albumService.Detail(song.getAlbumId());
                        Genre genre = genreService.Detail(song.getGenreId());

                        return ResponseEntity.status(HttpStatus.OK).body(ResponseObject.builder()
                                        .message("Reject song successfully")
                                        .status(HttpStatus.OK)
                                        .data(SongDetailResponse.fromSong(song, artist, album, genre))
                                        .build());
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseObject.builder()
                                        .message(e.getMessage())
                                        .status(HttpStatus.BAD_REQUEST)
                                        .data(null)
                                        .build());
                }
        }
}
