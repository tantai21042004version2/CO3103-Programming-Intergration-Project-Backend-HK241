package L03.CNPM.Music.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import L03.CNPM.Music.DTOS.playlist.ApprovePlalistDTO;
import L03.CNPM.Music.DTOS.playlist.CreatePlayListDTO;
import L03.CNPM.Music.DTOS.playlist.UploadSongToPlaylistDTO;
import L03.CNPM.Music.components.JwtTokenUtils;
import L03.CNPM.Music.models.User;
import L03.CNPM.Music.models.Genre;
import L03.CNPM.Music.models.Playlist;
import L03.CNPM.Music.responses.ResponseObject;
import L03.CNPM.Music.responses.playlist.PlaylistDetailResponse;
import L03.CNPM.Music.responses.playlist.PlaylistListResponse;
import L03.CNPM.Music.responses.playlist.PlaylistResponse;
import L03.CNPM.Music.responses.song.CloudinaryResponse;
import L03.CNPM.Music.services.playlist.IPlaylistService;
import L03.CNPM.Music.services.users.IUserService;
import L03.CNPM.Music.services.gerne.IGenreService;
import L03.CNPM.Music.services.song.ISongService;
import L03.CNPM.Music.models.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("${api.prefix}/playlists")
@RequiredArgsConstructor
public class PlaylistController {
    private final IPlaylistService playlistService;
    private final IUserService userService;
    private final IGenreService genreService;
    private final JwtTokenUtils jwtTokenUtils;
    private final ISongService songService;

    @GetMapping("/list")
    public ResponseEntity<ResponseObject> GetAll(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit) throws Exception {
        try {
            if (page < 1)
                page = 1;
            if (limit < 1)
                limit = 10;

            Pageable pageable = PageRequest.of(page - 1, limit);
            Page<Playlist> playlists = playlistService.Get(keyword, pageable);

            int totalPages = playlists.getTotalPages();
            int currentPage = playlists.getNumber() + 1;
            int itemsPerPage = playlists.getSize();

            List<PlaylistResponse> playlistResponses = new ArrayList<>();
            for (int index = 0; index < playlists.getContent().size(); index++) {
                User user = userService.Detail(playlists.getContent().get(index).getUserId());
                playlistResponses.add(PlaylistResponse.fromPlaylist(playlists.getContent().get(index), user));
            }

            PlaylistListResponse playlistListResponse = PlaylistListResponse.builder()
                    .playlistResponseList(playlistResponses)
                    .totalPages(totalPages)
                    .currentPage(currentPage)
                    .itemsPerPage(itemsPerPage)
                    .build();

            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Get all playlists successfully")
                    .status(HttpStatus.OK)
                    .data(playlistListResponse)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
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
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit) throws Exception {
        try {
            if (page < 1)
                page = 1;
            if (limit < 1)
                limit = 10;

            Pageable pageable = PageRequest.of(page - 1, limit);

            Page<Playlist> playlists = playlistService.GetPending(keyword, pageable);

            int totalPages = playlists.getTotalPages();
            int currentPage = playlists.getNumber() + 1;
            int itemsPerPage = playlists.getSize();

            List<PlaylistResponse> playlistResponses = new ArrayList<>();
            for (int index = 0; index < playlists.getContent().size(); index++) {
                User user = userService.Detail(playlists.getContent().get(index).getUserId());
                playlistResponses.add(PlaylistResponse.fromPlaylist(playlists.getContent().get(index), user));
            }

            PlaylistListResponse playlistListResponse = PlaylistListResponse.builder()
                    .playlistResponseList(playlistResponses)
                    .totalPages(totalPages)
                    .currentPage(currentPage)
                    .itemsPerPage(itemsPerPage)
                    .build();

            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Get pending playlists successfully")
                    .status(HttpStatus.OK)
                    .data(playlistListResponse)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .build());
        }
    }

    @GetMapping("/detail/{playlist_id}")
    public ResponseEntity<ResponseObject> Detail(@PathVariable("playlist_id") Long playlistId) throws Exception {
        try {
            Playlist playlist = playlistService.Detail(playlistId);
            User user = userService.Detail(playlist.getUserId());
            Genre genre = genreService.Detail(playlist.getGenreId());
            List<Song> songs = songService.GetByPlaylistId(playlist.getId());

            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Get playlist detail successfully")
                    .status(HttpStatus.OK)
                    .data(PlaylistDetailResponse.fromPlaylist(playlist, user, genre, songs))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .build());
        }
    }

    @PostMapping(value = "/cloudinary", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_LISTENER')")
    public ResponseEntity<ResponseObject> UploadCloudinary(
            @RequestPart("file") MultipartFile file,
            @RequestHeader("Authorization") String authorizationHeader) throws Exception {
        try {
            String token = authorizationHeader.substring(7);
            Long artistId = Long.parseLong(jwtTokenUtils.getUserId(token));

            Map<String, Object> response = playlistService.UploadCloudinary(file, artistId);

            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Upload playlist successfully")
                    .status(HttpStatus.OK)
                    .data(CloudinaryResponse.fromMap(response))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .build());
        }
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_LISTENER')")
    public ResponseEntity<ResponseObject> Create(
            @RequestBody CreatePlayListDTO createPlaylistDTO,
            @RequestHeader("Authorization") String authorizationHeader,
            BindingResult results) throws Exception {
        if (results.hasErrors()) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message("Invalid request")
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .build());
        }

        try {
            String token = authorizationHeader.substring(7);
            Long artistId = Long.parseLong(jwtTokenUtils.getUserId(token));

            Playlist playlist = playlistService.Create(createPlaylistDTO, artistId);

            Genre genre = genreService.Detail(createPlaylistDTO.getGenreId());
            User artist = userService.Detail(artistId);
            List<Song> songs = songService.GetByPlaylistId(playlist.getId());

            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Create playlist successfully")
                    .status(HttpStatus.OK)
                    .data(PlaylistDetailResponse.fromPlaylist(playlist, artist, genre, songs))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .build());
        }
    }

    @PatchMapping("/{playlist_id}/songs")
    @PreAuthorize("hasRole('ROLE_LISTENER')")
    public ResponseEntity<ResponseObject> UpdateSongs(
            @RequestBody UploadSongToPlaylistDTO uploadSongToPlaylistDTO,
            @PathVariable("playlist_id") Long playlistId,
            @RequestHeader("Authorization") String authorizationHeader) throws Exception {
        try {
            String token = authorizationHeader.substring(7);
            Long artistId = Long.parseLong(jwtTokenUtils.getUserId(token));

            Playlist playlist = playlistService.AddSong(uploadSongToPlaylistDTO, playlistId, artistId);

            Genre genre = genreService.Detail(playlist.getGenreId());
            User artist = userService.Detail(artistId);
            List<Song> songs = songService.GetByPlaylistId(playlist.getId());

            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Add song to playlist successfully")
                    .status(HttpStatus.OK)
                    .data(PlaylistDetailResponse.fromPlaylist(playlist, artist, genre, songs))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .build());
        }
    }

    @PatchMapping("/{playlist_id}/submit")
    @PreAuthorize("hasRole('ROLE_LISTENER')")
    public ResponseEntity<ResponseObject> Submit(@PathVariable("playlist_id") Long playlistId,
            @RequestHeader("Authorization") String authorizationHeader) throws Exception {
        try {
            String token = authorizationHeader.substring(7);
            Long artistId = Long.parseLong(jwtTokenUtils.getUserId(token));

            Playlist playlist = playlistService.SubmitPlaylist(playlistId, artistId);

            Genre genre = genreService.Detail(playlist.getGenreId());
            User artist = userService.Detail(artistId);
            List<Song> songs = songService.GetByPlaylistId(playlist.getId());

            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Submit playlist successfully")
                    .status(HttpStatus.OK)
                    .data(PlaylistDetailResponse.fromPlaylist(playlist, artist, genre, songs))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .build());
        }
    }

    @PatchMapping("/{playlist_id}/approve")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> Approve(
            @PathVariable("playlist_id") Long playlistId,
            @RequestBody ApprovePlalistDTO approvePlalistDTO) throws Exception {
        try {
            Playlist playlist = playlistService.Approve(playlistId, approvePlalistDTO);

            Genre genre = genreService.Detail(playlist.getGenreId());
            User artist = userService.Detail(playlist.getUserId());
            List<Song> songs = songService.GetByPlaylistId(playlist.getId());

            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Approve playlist successfully")
                    .status(HttpStatus.OK)
                    .data(PlaylistDetailResponse.fromPlaylist(playlist, artist, genre, songs))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .build());
        }
    }

    @DeleteMapping("/{playlist_id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_LISTENER')")
    public ResponseEntity<ResponseObject> Delete(
            @PathVariable("playlist_id") Long playlistId) throws Exception {
        try {
            playlistService.Delete(playlistId);

            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Delete playlist successfully")
                    .status(HttpStatus.OK)
                    .data(null)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .build());
        }
    }

}
