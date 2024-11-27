package L03.CNPM.Music.services.playlist;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import L03.CNPM.Music.DTOS.playlist.CreatePlayListDTO;
import L03.CNPM.Music.DTOS.playlist.UpdatePlaylistDTO;
import L03.CNPM.Music.exceptions.DataNotFoundException;
import L03.CNPM.Music.exceptions.UploadCloudinaryException;
import L03.CNPM.Music.models.Playlist;
import L03.CNPM.Music.models.Song;
import L03.CNPM.Music.models.SongPlaylist;
import L03.CNPM.Music.models.User;
import L03.CNPM.Music.models.Genre;
import L03.CNPM.Music.repositories.PlaylistRepository;
import L03.CNPM.Music.repositories.UserRepository;
import L03.CNPM.Music.repositories.SongRepository;
import L03.CNPM.Music.repositories.GenreRepository;
import L03.CNPM.Music.repositories.SongPlaylistRepository;
import L03.CNPM.Music.utils.DateUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaylistService implements IPlaylistService {
    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final GenreRepository genreRepository;
    private final SongPlaylistRepository songPlaylistRepository;
    private final Cloudinary cloudinary;
    private final DateUtils dateUtils;

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> UploadCloudinary(MultipartFile file) throws Exception {
        Map<String, Object> response = null;

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File does not exist or is empty.");
        }

        try {
            Map<String, Object> uploadParams = ObjectUtils.asMap(
                    "resource_type", "image",
                    "folder", "playlists");

            response = cloudinary.uploader().upload(file.getBytes(), uploadParams);
        } catch (Exception e) {
            throw new UploadCloudinaryException(e.getMessage());
        }

        if (response == null || response.isEmpty()) {
            throw new UploadCloudinaryException("Failed to upload file to Cloudinary.");
        }

        return response;
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
    public Playlist Create(CreatePlayListDTO createPlaylistDTO, String userId) throws Exception {
        Optional<User> user = userRepository.findById(Long.parseLong(userId));
        if (user.isEmpty()) {
            throw new DataNotFoundException("User not found");
        }

        Playlist playlist = Playlist.builder()
                .name(createPlaylistDTO.getName())
                .description(createPlaylistDTO.getDescription())
                .coverUrl(createPlaylistDTO.getCoverUrl())
                .userId(user.get().getId())
                .isPublic(createPlaylistDTO.getIsPublic())
                .status(Playlist.Status.DRAFT)
                .createdAt(dateUtils.getCurrentDate())
                .updatedAt(dateUtils.getCurrentDate())
                .build();

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
                    .song(song.get())
                    .playlist(playlist.get())
                    .build();

            songPlaylistRepository.save(songPlaylist);
        }

        for (Long songId : updatePlaylistDTO.getRemoveSongIds()) {
            Optional<SongPlaylist> songPlaylist = songPlaylistRepository.findBySongIdAndPlaylistId(songId, playlistId);
            if (songPlaylist.isEmpty()) {
                throw new DataNotFoundException("Song not found");
            }

            songPlaylistRepository.deleteById(songPlaylist.get().getId());
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
}
