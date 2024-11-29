package L03.CNPM.Music.services.playlist;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import L03.CNPM.Music.DTOS.playlist.ApprovePlalistDTO;
import L03.CNPM.Music.DTOS.playlist.CreatePlayListDTO;
import L03.CNPM.Music.DTOS.playlist.UpdatePlaylistDTO;
import L03.CNPM.Music.DTOS.playlist.UploadSongToPlaylistDTO;
import L03.CNPM.Music.components.LocalizationUtils;
import L03.CNPM.Music.exceptions.DataNotFoundException;
import L03.CNPM.Music.exceptions.UploadCloudinaryException;
import L03.CNPM.Music.models.Playlist;
import L03.CNPM.Music.models.Role;
import L03.CNPM.Music.models.Song;
import L03.CNPM.Music.models.SongPlaylist;
import L03.CNPM.Music.models.User;
import L03.CNPM.Music.models.Genre;
import L03.CNPM.Music.repositories.PlaylistRepository;
import L03.CNPM.Music.repositories.UserRepository;
import L03.CNPM.Music.utils.MessageKeys;
import L03.CNPM.Music.repositories.SongRepository;
import L03.CNPM.Music.repositories.GenreRepository;
import L03.CNPM.Music.repositories.SongPlaylistRepository;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class PlaylistService implements IPlaylistService {
    private final PlaylistRepository playlistRepository;
    private final SongPlaylistRepository songPlaylistRepository;
    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final GenreRepository genreRepository;
    private final Cloudinary cloudinary;
    private final LocalizationUtils localizationUtils;

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> UploadCloudinary(MultipartFile file, Long artistId) throws Exception {
        Optional<User> existedArtist = userRepository.findById(artistId);
        if (existedArtist.isEmpty()) {
            throw new DataNotFoundException("Artist with ID %s not found".formatted(artistId));
        }

        if (!existedArtist.get().getRole().getName().equals(Role.LISTENER)) {
            throw new DataNotFoundException("User with ID %s is not a listener".formatted(artistId));
        }

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File does not exist or is empty.");
        }

        Map<String, Object> response = null;
        Map<String, Object> uploadParams = ObjectUtils.asMap(
                "resource_type", "image",
                "folder", "playlists");

        try {
            response = cloudinary.uploader().upload(file.getBytes(), uploadParams);
        } catch (Exception e) {
            throw new UploadCloudinaryException(
                    localizationUtils.getLocalizedMessage(MessageKeys.CLOUDINARY_UPLOAD_FAIL) + ": " + e.getMessage());
        }

        if (response == null || response.isEmpty()) {
            throw new UploadCloudinaryException(
                    localizationUtils.getLocalizedMessage(MessageKeys.CLOUDINARY_UPLOAD_FAIL));
        }

        return response;
    }

    @Override
    public Playlist Create(CreatePlayListDTO createPlaylistDTO, Long artistId) throws Exception {
        Optional<User> existedArtist = userRepository.findById(artistId);
        if (existedArtist.isEmpty()) {
            throw new DataNotFoundException("Artist with ID %s not found".formatted(artistId));
        }

        Optional<Genre> genre = genreRepository.findById(createPlaylistDTO.getGenreId());
        if (genre.isEmpty()) {
            throw new DataNotFoundException("Genre with ID %s not found".formatted(createPlaylistDTO.getGenreId()));
        }

        Playlist playlist = Playlist.builder()
                .name(createPlaylistDTO.getName())
                .description(createPlaylistDTO.getDescription())
                .coverUrl(createPlaylistDTO.getCoverUrl())
                .userId(existedArtist.get().getId())
                .genreId(createPlaylistDTO.getGenreId())
                .isPublic(createPlaylistDTO.getIsPublic())
                .status("DRAFT")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return playlistRepository.save(playlist);
    }

    @Override
    public Playlist AddSong(UploadSongToPlaylistDTO uploadSongToPlaylistDTO, Long playlistId, Long artistId)
            throws Exception {
        Optional<Playlist> existedPlaylist = playlistRepository.findById(playlistId);
        if (existedPlaylist.isEmpty()) {
            throw new DataNotFoundException("Playlist with ID %s not found".formatted(playlistId));
        }

        Playlist playlist = existedPlaylist.get();

        Optional<User> existedArtist = userRepository.findById(artistId);
        if (existedArtist.isEmpty()) {
            throw new DataNotFoundException("Artist with ID %s not found".formatted(artistId));
        }

        if (!playlist.getUserId().equals(artistId)) {
            throw new DataNotFoundException("Artist with ID %s is not the owner of the playlist".formatted(artistId));
        }

        for (Long songId : uploadSongToPlaylistDTO.getAddSongIds()) {
            Optional<Song> existedSong = songRepository.findById(songId);
            if (existedSong.isEmpty()) {
                throw new DataNotFoundException("Song with ID %s not found".formatted(songId));
            }

            Song song = existedSong.get();

            SongPlaylist songPlaylist = SongPlaylist.builder()
                    .songId(song.getId())
                    .playlistId(playlist.getId())
                    .build();

            songPlaylistRepository.save(songPlaylist);
        }

        for (Long songId : uploadSongToPlaylistDTO.getRemoveSongIds()) {
            Optional<SongPlaylist> songPlaylist = songPlaylistRepository.findBySongIdAndPlaylistId(songId, playlistId);
            if (songPlaylist.isEmpty()) {
                throw new DataNotFoundException("Song with ID %s not found in playlist with ID %s".formatted(songId,
                        playlistId));
            }

            songPlaylistRepository.delete(songPlaylist.get());
        }

        return playlist;
    }

    @Override
    public Playlist SubmitPlaylist(Long playlistId, Long artistId) throws Exception {
        Optional<Playlist> existedPlaylist = playlistRepository.findById(playlistId);
        if (existedPlaylist.isEmpty()) {
            throw new DataNotFoundException("Playlist with ID %s not found".formatted(playlistId));
        }

        Playlist playlist = existedPlaylist.get();

        if (!playlist.getUserId().equals(artistId)) {
            throw new DataNotFoundException("Artist with ID %s is not the owner of the playlist".formatted(artistId));
        }
        if (playlist.getStatus().equals("PENDING")) {
            throw new DataNotFoundException("Playlist with ID %s is already pending".formatted(playlistId));
        }

        playlist.setStatus("PENDING");
        playlist.setIsPublic(true);
        playlist.setUpdatedAt(LocalDateTime.now());

        return playlistRepository.save(playlist);
    }

    @Override
    public Playlist Detail(Long playlistId) throws Exception {
        Optional<Playlist> playlist = playlistRepository.findById(playlistId);
        if (playlist.isEmpty()) {
            throw new DataNotFoundException("Playlist not found");
        }

        return playlist.get();
    }

    @Override
    public Playlist Approve(Long playlistId, ApprovePlalistDTO approvePlalistDTO) throws Exception {
        Optional<Playlist> existedPlaylist = playlistRepository.findById(playlistId);
        if (existedPlaylist.isEmpty()) {
            throw new DataNotFoundException("Playlist with ID %s not found".formatted(playlistId));
        }
        Playlist playlist = existedPlaylist.get();

        if (approvePlalistDTO.getIsApproved()) {
            playlist.setStatus("APPROVED");
        } else {
            playlist.setStatus("REJECTED");
        }

        playlist.setDescription(approvePlalistDTO.getDescription());
        playlist.setUpdatedAt(LocalDateTime.now());

        return playlistRepository.save(playlist);
    }

    @Override
    public Playlist Update(Long playlistId, UpdatePlaylistDTO updatePlaylistDTO) throws Exception {
        Optional<Playlist> playlist = playlistRepository.findById(playlistId);
        if (playlist.isEmpty()) {
            throw new DataNotFoundException("Playlist not found");
        }

        for (Long songId : updatePlaylistDTO.getAddSongIds()) {
            Optional<Song> song = songRepository.findById(songId);
            if (song.isEmpty()) {
                throw new DataNotFoundException("Song not found");
            }

            SongPlaylist songPlaylist = SongPlaylist.builder()
                    .songId(song.get().getId())
                    .playlistId(playlist.get().getId())
                    .build();

            songPlaylistRepository.save(songPlaylist);
        }

        for (Long songId : updatePlaylistDTO.getRemoveSongIds()) {
            Optional<SongPlaylist> songPlaylist = songPlaylistRepository.findBySongIdAndPlaylistId(songId, playlistId);
            if (songPlaylist.isEmpty()) {
                throw new DataNotFoundException("Song not found");
            }

            songPlaylistRepository.delete(songPlaylist.get());
        }

        Optional<Genre> genre = genreRepository.findById(updatePlaylistDTO.getGenreId());
        if (genre.isEmpty()) {
            throw new DataNotFoundException("Genre not found");
        }

        if (updatePlaylistDTO.getIsPublic() == null) {
            updatePlaylistDTO.setIsPublic(playlist.get().getIsPublic());
        }

        if (updatePlaylistDTO.getDescription() == null) {
            updatePlaylistDTO.setDescription(playlist.get().getDescription());
        }

        return null;
    }

    @Override
    public Page<Playlist> Get(String keyword, Pageable pageable) {
        return playlistRepository.get(keyword, pageable);
    }

    @Override
    public Page<Playlist> GetPending(String keyword, Pageable pageable) {
        return playlistRepository.getPending(keyword, pageable);
    }

    @Override
    public void Delete(Long playlistId) throws Exception {
        Optional<Playlist> existedPlaylist = playlistRepository.findById(playlistId);
        if (existedPlaylist.isEmpty()) {
            throw new DataNotFoundException("Playlist not found");
        }

        Playlist playlist = existedPlaylist.get();

        if (playlist.getDeletedAt() != null) {
            throw new DataNotFoundException("Playlist with ID %s is already deleted".formatted(playlistId));
        }

        playlist.setDeletedAt(LocalDateTime.now());
        playlistRepository.save(playlist);
    }
}
