package L03.CNPM.Music.controllers;

import L03.CNPM.Music.DTOS.album.UploadSongToAlbumDTO;
import L03.CNPM.Music.DTOS.album.UploadAlbumDTO;
import L03.CNPM.Music.exceptions.DataNotFoundException;
import L03.CNPM.Music.models.Album;
import L03.CNPM.Music.responses.ResponseObject;
import L03.CNPM.Music.responses.album.AlbumDetailResponse;
import L03.CNPM.Music.responses.song.SongResponse;
import L03.CNPM.Music.services.album.AlbumService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/albums")
public class AlbumController {
        private final AlbumService albumService;
        @Value("${jwt.secretKey}")
        private String SECRETKEY;

        @PostMapping("")
        @PreAuthorize("hasRole('ROLE_ARTIST')")
        public ResponseEntity<ResponseObject> uploadAlbum(
                        @RequestBody UploadAlbumDTO uploadAlbumDTO,
                        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
                String token = authorizationHeader.substring(7);
                SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRETKEY));
                Claims claims = Jwts.parser()
                                .verifyWith(secretKey)
                                .build()
                                .parseSignedClaims(token)
                                .getPayload();
                Long artistId = Long.valueOf(claims.get("userId", String.class));
                Album album = albumService.uploadAlbum(uploadAlbumDTO, artistId);

                // List<Song> songs = albumService.getSongsByAlbumId(album.getId());

                // User artist = userService.getUserById(artistId);

                return ResponseEntity.ok().body(
                                ResponseObject.builder()
                                                .message("upload album successfully")
                                                .status(HttpStatus.OK)
                                                .data(AlbumDetailResponse.fromAlbum(album, null, null))
                                                .build());

        }

        @PatchMapping("/{albumId}/songs")
        @PreAuthorize("hasRole('ROLE_ARTIST')")
        public ResponseEntity<ResponseObject> addSongToAlbum(
                        @RequestBody UploadSongToAlbumDTO addSongAlbumDTO,
                        @PathVariable Long albumId) {
                try {
                        List<SongResponse> songs = albumService.uploadSongToAlbum(addSongAlbumDTO, albumId);
                        Album album = albumService.Detail(albumId);

                        return ResponseEntity.ok().body(
                                        ResponseObject.builder()
                                                        .message("add song to album successfully")
                                                        .status(HttpStatus.OK)
                                                        .data(AlbumDetailResponse.fromAlbum(album, null, null))
                                                        .build());
                } catch (DataNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseObject.builder()
                                        .message(e.getMessage())
                                        .status(HttpStatus.BAD_REQUEST)
                                        .data(null)
                                        .build());
                }
        }
}
