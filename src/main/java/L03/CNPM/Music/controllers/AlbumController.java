package L03.CNPM.Music.controllers;

import L03.CNPM.Music.components.JwtTokenUtils;
import L03.CNPM.Music.DTOS.album.UploadAlbumDTO;
import L03.CNPM.Music.DTOS.album.UploadSongToAlbumDTO;
import L03.CNPM.Music.models.Album;
import L03.CNPM.Music.responses.ResponseObject;
import L03.CNPM.Music.responses.album.AlbumDetailResponse;
import L03.CNPM.Music.responses.album.AlbumListResponse;
import L03.CNPM.Music.responses.album.AlbumResponse;
import L03.CNPM.Music.responses.song.CloudinaryResponse;
import L03.CNPM.Music.services.album.IAlbumService;
import L03.CNPM.Music.services.gerne.IGenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import L03.CNPM.Music.models.Song;
import L03.CNPM.Music.models.User;
import L03.CNPM.Music.services.users.IUserService;
import L03.CNPM.Music.services.song.ISongService;

import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import L03.CNPM.Music.models.Genre;
import L03.CNPM.Music.DTOS.album.ApproveAlbumDTO;

import java.util.ArrayList;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/albums")
public class AlbumController {
        private final IAlbumService albumService;
        private final JwtTokenUtils jwtTokenUtils;
        private final IUserService userService;
        private final ISongService songService;
        private final IGenreService genreService;

        @GetMapping("/list")
        public ResponseEntity<ResponseObject> GetAll(
                        @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                        @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                        @RequestParam(value = "status", required = false, defaultValue = "") Album.Status status,
                        @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit) {
                try {
                        if (page < 1)
                                page = 1;
                        if (limit < 1)
                                limit = 10;

                        Pageable pageable = PageRequest.of(page - 1, limit);
                        Page<Album> albums = albumService.Get(keyword, status, pageable);

                        int totalPages = albums.getTotalPages();
                        int currentPage = albums.getNumber() + 1;
                        int itemsPerPage = albums.getSize();

                        List<AlbumResponse> albumResponses = new ArrayList<>();
                        for (int index = 0; index < albums.getContent().size(); index++) {
                                Genre genre = genreService.Detail(albums.getContent().get(index).getGenreId());
                                albumResponses.add(AlbumResponse.fromAlbum(albums.getContent().get(index), genre));
                        }

                        AlbumListResponse albumListResponse = AlbumListResponse.builder()
                                        .albumResponseList(albumResponses)
                                        .totalPages(totalPages)
                                        .currentPage(currentPage)
                                        .itemsPerPage(itemsPerPage)
                                        .build();

                        return ResponseEntity.ok().body(ResponseObject.builder()
                                        .message("Get albums successfully")
                                        .status(HttpStatus.OK)
                                        .data(albumListResponse)
                                        .build());
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseObject.builder()
                                        .message(e.getMessage())
                                        .status(HttpStatus.BAD_REQUEST)
                                        .data(null)
                                        .build());
                }
        }

        @GetMapping("/me")
        @PreAuthorize("hasRole('ROLE_ARTIST')")
        public ResponseEntity<ResponseObject> GetArtist(
                        @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                        @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                        @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
                        @RequestParam(value = "status", required = false, defaultValue = "") Album.Status status,
                        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
                try {
                        String extractedToken = authorizationHeader.substring(7);
                        Long artistId = Long.parseLong(jwtTokenUtils.getUserId(extractedToken));

                        if (page < 1)
                                page = 1;
                        if (limit < 1)
                                limit = 10;

                        Pageable pageable = PageRequest.of(page - 1, limit);

                        Page<Album> albums = albumService.GetByArtistId(artistId, pageable, keyword, status);

                        int totalPages = albums.getTotalPages();
                        int currentPage = albums.getNumber() + 1;
                        int itemsPerPage = albums.getSize();

                        List<AlbumResponse> albumResponses = new ArrayList<>();
                        for (int index = 0; index < albums.getContent().size(); index++) {
                                Genre genre = genreService.Detail(albums.getContent().get(index).getGenreId());
                                albumResponses.add(AlbumResponse.fromAlbum(albums.getContent().get(index), genre));
                        }

                        AlbumListResponse albumListResponse = AlbumListResponse.builder()
                                        .albumResponseList(albumResponses)
                                        .totalPages(totalPages)
                                        .currentPage(currentPage)
                                        .itemsPerPage(itemsPerPage)
                                        .build();

                        return ResponseEntity.ok().body(ResponseObject.builder()
                                        .message("Get albums successfully")
                                        .status(HttpStatus.OK)
                                        .data(albumListResponse)
                                        .build());
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseObject.builder()
                                        .message(e.getMessage())
                                        .status(HttpStatus.BAD_REQUEST)
                                        .data(null)
                                        .build());
                }
        }

        @GetMapping("/pending")
        @PreAuthorize("hasRole('ROLE_ADMIN')")
        public ResponseEntity<ResponseObject> GetPending(
                        @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                        @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                        @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit) {
                try {
                        if (page < 1)
                                page = 1;
                        if (limit < 1)
                                limit = 10;

                        Pageable pageable = PageRequest.of(page - 1, limit);

                        Page<Album> albums = albumService.Get(keyword, Album.Status.PENDING, pageable);

                        int totalPages = albums.getTotalPages();
                        int currentPage = albums.getNumber() + 1;
                        int itemsPerPage = albums.getSize();

                        List<AlbumResponse> albumResponses = new ArrayList<>();
                        for (int index = 0; index < albums.getContent().size(); index++) {
                                Genre genre = genreService.Detail(albums.getContent().get(index).getGenreId());
                                albumResponses.add(AlbumResponse.fromAlbum(albums.getContent().get(index), genre));
                        }

                        AlbumListResponse albumListResponse = AlbumListResponse.builder()
                                        .albumResponseList(albumResponses)
                                        .totalPages(totalPages)
                                        .currentPage(currentPage)
                                        .itemsPerPage(itemsPerPage)
                                        .build();

                        return ResponseEntity.ok().body(ResponseObject.builder()
                                        .message("Get pending albums successfully")
                                        .status(HttpStatus.OK)
                                        .data(albumListResponse)
                                        .build());
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseObject.builder()
                                        .message(e.getMessage())
                                        .status(HttpStatus.BAD_REQUEST)
                                        .data(null)
                                        .build());
                }
        }

        @GetMapping("/detail/{album_id}")
        public ResponseEntity<ResponseObject> Detail(
                        @PathVariable("album_id") Long albumId) {
                try {
                        Album album = albumService.Detail(albumId);
                        List<Song> songs = songService.GetByAlbumtId(albumId);
                        User artist = userService.Detail(album.getArtistId());
                        Genre genre = genreService.Detail(album.getGenreId());

                        return ResponseEntity.ok().body(ResponseObject.builder()
                                        .message("Get album detail successfully")
                                        .status(HttpStatus.OK)
                                        .data(AlbumDetailResponse.fromAlbum(album, songs, artist, genre))
                                        .build());
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseObject.builder()
                                        .message(e.getMessage())
                                        .status(HttpStatus.BAD_REQUEST)
                                        .data(null)
                                        .build());
                }
        }

        @PostMapping(value = "/cloudinary", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        @PreAuthorize("hasRole('ROLE_ARTIST')")
        public ResponseEntity<ResponseObject> UploadCloudinary(
                        @RequestParam("file") MultipartFile file,
                        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader)
                        throws Exception {
                try {
                        String extractedToken = authorizationHeader.substring(7);
                        Long artistId = Long.parseLong(jwtTokenUtils.getUserId(extractedToken));

                        Map<String, Object> result = albumService.UploadCloudinary(file, artistId);

                        return ResponseEntity.ok().body(ResponseObject.builder()
                                        .message("Upload cloudinary successfully")
                                        .status(HttpStatus.OK)
                                        .data(CloudinaryResponse.fromMap(result))
                                        .build());
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseObject.builder()
                                        .message("Upload cloudinary successfully")
                                        .status(HttpStatus.OK)
                                        .data(null)
                                        .build());
                }
        }

        @PostMapping("")
        @PreAuthorize("hasRole('ROLE_ARTIST')")
        public ResponseEntity<ResponseObject> Create(
                        @RequestBody UploadAlbumDTO uploadAlbumDTO,
                        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                        BindingResult result) throws Exception {

                if (result.hasErrors()) {
                        return ResponseEntity.badRequest().body(ResponseObject.builder()
                                        .message("Invalid request")
                                        .status(HttpStatus.BAD_REQUEST)
                                        .data(null)
                                        .build());
                }

                try {
                        String extractedToken = authorizationHeader.substring(7);
                        Long artistId = Long.parseLong(jwtTokenUtils.getUserId(extractedToken));

                        Album album = albumService.Create(uploadAlbumDTO, artistId);

                        List<Song> songs = songService.GetByAlbumtId(album.getId());
                        Genre genre = genreService.Detail(album.getGenreId());
                        User artist = userService.Detail(artistId);

                        return ResponseEntity.ok().body(ResponseObject.builder()
                                        .message("Create album successfully")
                                        .status(HttpStatus.OK)
                                        .data(AlbumDetailResponse.fromAlbum(album, songs, artist, genre))
                                        .build());
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseObject.builder()
                                        .message(e.getMessage())
                                        .status(HttpStatus.BAD_REQUEST)
                                        .data(null)
                                        .build());
                }
        }

        @PatchMapping("/{album_id}/songs")
        @PreAuthorize("hasRole('ROLE_ARTIST')")
        public ResponseEntity<ResponseObject> UpdateSongs(
                        @RequestBody UploadSongToAlbumDTO addSongAlbumDTO,
                        @PathVariable("album_id") Long albumId,
                        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
                try {
                        String extractedToken = authorizationHeader.substring(7);
                        Long artistId = Long.parseLong(jwtTokenUtils.getUserId(extractedToken));

                        Album album = albumService.AddSong(addSongAlbumDTO, albumId, artistId);
                        List<Song> songs = songService.GetByAlbumtId(albumId);
                        Genre genre = genreService.Detail(album.getGenreId());
                        User artist = userService.Detail(artistId);

                        return ResponseEntity.ok().body(ResponseObject.builder()
                                        .message("Update songs to album successfully")
                                        .status(HttpStatus.OK)
                                        .data(AlbumDetailResponse.fromAlbum(album, songs, artist, genre))
                                        .build());
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseObject.builder()
                                        .message(e.getMessage())
                                        .status(HttpStatus.BAD_REQUEST)
                                        .data(null)
                                        .build());
                }
        }

        @PatchMapping("/{album_id}/submit")
        @PreAuthorize("hasRole('ROLE_ARTIST')")
        public ResponseEntity<ResponseObject> Submit(
                        @PathVariable("album_id") Long albumId,
                        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
                try {
                        String extractedToken = authorizationHeader.substring(7);
                        Long artistId = Long.parseLong(jwtTokenUtils.getUserId(extractedToken));

                        Album album = albumService.SubmitAlbum(albumId, artistId);
                        List<Song> songs = songService.GetByAlbumtId(albumId);
                        Genre genre = genreService.Detail(album.getGenreId());
                        User artist = userService.Detail(artistId);

                        return ResponseEntity.ok().body(ResponseObject.builder()
                                        .message("Submit album successfully")
                                        .status(HttpStatus.OK)
                                        .data(AlbumDetailResponse.fromAlbum(album, songs, artist, genre))
                                        .build());
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseObject.builder()
                                        .message(e.getMessage())
                                        .status(HttpStatus.BAD_REQUEST)
                                        .data(null)
                                        .build());
                }
        }

        @PatchMapping("/{album_id}")
        @PreAuthorize("hasRole('ROLE_ADMIN')")
        public ResponseEntity<ResponseObject> Approve(
                        @PathVariable("album_id") Long albumId,
                        @RequestBody ApproveAlbumDTO approveAlbumDTO) {
                try {

                        Album album = albumService.Approve(albumId, approveAlbumDTO);
                        List<Song> songs = songService.GetByAlbumtId(albumId);
                        Genre genre = genreService.Detail(album.getGenreId());
                        User artist = userService.Detail(album.getArtistId());

                        return ResponseEntity.ok().body(ResponseObject.builder()
                                        .message("Approve album successfully")
                                        .status(HttpStatus.OK)
                                        .data(AlbumDetailResponse.fromAlbum(album, songs, artist, genre))
                                        .build());
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseObject.builder()
                                        .message(e.getMessage())
                                        .status(HttpStatus.BAD_REQUEST)
                                        .data(null)
                                        .build());
                }
        }

        @DeleteMapping("/{album_id}")
        @PreAuthorize("hasRole('ROLE_ARTIST') or hasRole('ROLE_ADMIN')")
        public ResponseEntity<ResponseObject> Delete(
                        @PathVariable("album_id") Long albumId) {
                try {
                        albumService.Delete(albumId);

                        return ResponseEntity.ok().body(ResponseObject.builder()
                                        .message("Delete album successfully")
                                        .status(HttpStatus.OK)
                                        .data(null)
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
