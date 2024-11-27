package L03.CNPM.Music.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import L03.CNPM.Music.DTOS.playlist.CreatePlayListDTO;
import L03.CNPM.Music.DTOS.playlist.UpdatePlaylistDTO;
import L03.CNPM.Music.models.User;
import L03.CNPM.Music.models.Genre;
import L03.CNPM.Music.models.Playlist;
import L03.CNPM.Music.responses.ResponseObject;
import L03.CNPM.Music.responses.playlist.PlaylistDetailResponse;
import L03.CNPM.Music.responses.song.CloudinaryResponse;
import L03.CNPM.Music.services.playlist.IPlaylistService;
import L03.CNPM.Music.services.users.IUserService;
import L03.CNPM.Music.services.gerne.IGenreService;

@RestController
@RequestMapping("${api.prefix}/playlists")
@RequiredArgsConstructor
public class PlaylistController {
    private final IPlaylistService playlistService;
    private final IUserService userService;
    private final IGenreService genreService;

    @GetMapping("/{playlist_id}")
    @PreAuthorize("hasRole('ROLE_LISTENER')")
    public ResponseEntity<ResponseObject> Detail(@PathVariable Long playlistId) throws Exception {
        try {
            Playlist playlist = playlistService.Detail(playlistId);

            User user = userService.Detail(playlist.getUserId());

            Genre genre = genreService.Detail(playlist.getGenreId());

            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Get playlist detail successfully")
                    .status(HttpStatus.OK)
                    .data(PlaylistDetailResponse.fromPlaylist(playlist, user, genre))
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
            @RequestPart("file") MultipartFile file) throws Exception {
        try {
            Map<String, Object> response = playlistService.UploadCloudinary(file);

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
            User user = userService.GetUserDetailByToken(token);

            Playlist playlist = playlistService.Create(createPlaylistDTO, user.getId().toString());

            Genre genre = genreService.Detail(createPlaylistDTO.getGenreId());

            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Create playlist successfully")
                    .status(HttpStatus.OK)
                    .data(PlaylistDetailResponse.fromPlaylist(playlist, user, genre))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .build());
        }
    }

    @PatchMapping("/{playlist_id}")
    @PreAuthorize("hasRole('ROLE_LISTENER')")
    public ResponseEntity<ResponseObject> Update(
            @PathVariable Long playlistId,
            @RequestBody UpdatePlaylistDTO updatePlaylistDTO,
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
            User user = userService.GetUserDetailByToken(token);

            Playlist playlist = playlistService.Update(playlistId, updatePlaylistDTO);

            Genre genre = genreService.Detail(updatePlaylistDTO.getGenreId());

            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Update playlist successfully")
                    .status(HttpStatus.OK)
                    .data(PlaylistDetailResponse.fromPlaylist(playlist, user, genre))
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
