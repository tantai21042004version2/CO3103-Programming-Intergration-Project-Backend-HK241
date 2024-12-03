package L03.CNPM.Music.services.song;

import java.io.File;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import L03.CNPM.Music.DTOS.song.SongMetadataDTO;
import L03.CNPM.Music.exceptions.DataNotFoundException;
import L03.CNPM.Music.models.Song;
import L03.CNPM.Music.models.SongPlaylist;
import L03.CNPM.Music.models.User;
import L03.CNPM.Music.models.Genre;
import L03.CNPM.Music.models.Role;
import L03.CNPM.Music.repositories.SongRepository;
import L03.CNPM.Music.repositories.GenreRepository;
import L03.CNPM.Music.repositories.SongPlaylistRepository;
import L03.CNPM.Music.repositories.UserRepository;
import L03.CNPM.Music.utils.AudioFileUtils;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class SongService implements ISongService {
    private final AudioFileUtils audioFileUtils;
    private final Cloudinary cloudinary;
    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final SongPlaylistRepository songPlaylistRepository;

    @Override
    public Page<Song> Get(String keyword, Pageable pageable) {
        if (keyword != null) {
            keyword = keyword.trim();
            if (keyword.isEmpty()) {
                keyword = null;
            }
        }
        return songRepository.findAll(keyword, pageable);
    }

    @Override
    public Page<Song> GetPending(String keyword, Pageable pageable) {
        if (keyword != null) {
            keyword = keyword.trim();
            if (keyword.isEmpty()) {
                keyword = null;
            }
        }
        return songRepository.findAllPending(keyword, pageable);
    }

    @Override
    public Page<Song> GetByArtirstId(String artistId, Pageable pageable) {
        return songRepository.findAllByArtistId(Long.parseLong(artistId), pageable);
    }

    @Override
    public List<Song> GetByAlbumtId(Long albumId) {
        return songRepository.findAllByAlbumId(albumId);
    }

    @Override
    public List<Song> GetByPlaylistId(Long playlistId) {
        List<SongPlaylist> songPlaylists = songPlaylistRepository.findByPlaylistId(playlistId);

        List<Song> songs = new ArrayList<>();

        for (SongPlaylist songPlaylist : songPlaylists) {
            Optional<Song> song = songRepository.findById(songPlaylist.getSongId());
            if (song.isPresent()) {
                songs.add(song.get());
            }
        }

        return songs;
    }

    @Override
    public Song ApproveSong(String id) throws Exception {
        Optional<Song> existingSong = songRepository.findById(Long.parseLong(id));
        if (existingSong.isEmpty()) {
            throw new DataNotFoundException("Song not found.");
        }
        Song song = existingSong.get();

        song.setStatus(Song.Status.APPROVED);

        if (song.getCreatedAt() == null) {
            song.setCreatedAt(LocalDateTime.now());
        }
        song.setUpdatedAt(LocalDateTime.now());

        return songRepository.save(song);
    }

    @Override
    public Song RejectSong(String id) throws Exception {
        Optional<Song> existingSong = songRepository.findById(Long.parseLong(id));
        if (existingSong.isEmpty()) {
            throw new DataNotFoundException("Song not found.");
        }
        Song song = existingSong.get();

        song.setStatus(Song.Status.REJECTED);
        if (song.getCreatedAt() == null) {
            song.setCreatedAt(LocalDateTime.now());
        }
        song.setUpdatedAt(LocalDateTime.now());

        return songRepository.save(song);
    }

    @Override
    public Map<String, Object> UploadSong(MultipartFile file) throws Exception {
        Map<String, Object> response = null;

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File does not exist or is empty.");
        }

        String fileType = file.getContentType();
        if (!audioFileUtils.isValidAudioFile(fileType, file.getOriginalFilename())) {
            throw new IllegalArgumentException("File type is not supported: " + fileType);
        }

        File tempFile = audioFileUtils.convertMultipartFileToFile(file);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadParams = ObjectUtils.asMap(
                    "resource_type", "video",
                    "folder", "songs");

            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary
                    .uploader().upload(tempFile, uploadParams);

            response = Map.of(
                    "secure_url", uploadResult.get("secure_url"),
                    "public_id", uploadResult.get("public_id"),
                    "duration", uploadResult.get("duration"));
        } finally {
            tempFile.delete();
        }
        return response;
    }

    @Override
    public Song Create(SongMetadataDTO metadataSongDTO) throws Exception {
        Optional<User> existingArtist = userRepository.findById(metadataSongDTO.getArtistId());
        if (existingArtist.isEmpty()) {
            throw new DataNotFoundException("Artist not found.");
        }

        if (!existingArtist.get().getRole().getName().equals(Role.ARTIST)) {
            throw new DataNotFoundException("You are not an artist.");
        }

        if (existingArtist.get().getImageUrl() == null) {
            throw new DataNotFoundException("You have not set your profile image yet, please set it first.");
        }

        Optional<Genre> existingGenre = genreRepository.findById(metadataSongDTO.getGenreId());
        if (existingGenre.isEmpty()) {
            throw new DataNotFoundException("Genre not found.");
        }

        Song newSong = Song.builder()
                .name(metadataSongDTO.getName())
                .description(metadataSongDTO.getDescription())
                .releaseDate(LocalDate.parse(metadataSongDTO.getReleaseDate()))
                .artistId(metadataSongDTO.getArtistId())
                .genreId(metadataSongDTO.getGenreId())
                .duration(metadataSongDTO.getDuration())
                .publicId(metadataSongDTO.getPublicId())
                .secureUrl(metadataSongDTO.getSecureUrl())
                .status(Song.Status.DRAFT)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        if (existingArtist.get().getImageUrl() != null) {
            newSong.setImageUrl(existingArtist.get().getImageUrl());
        }

        return songRepository.save(newSong);
    }

    @Override
    public Song Detail(Long id) throws Exception {
        Optional<Song> existingSong = songRepository.findById(id);
        if (existingSong.isEmpty()) {
            throw new DataNotFoundException("Song not found.");
        }
        return existingSong.get();
    }

    @Override
    @Transactional
    public void Delete(String publicId) throws Exception {
        if (publicId == null || publicId.isEmpty()) {
            throw new IllegalArgumentException("Public ID is invalid.");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> deleteResult = cloudinary.uploader().destroy(publicId, ObjectUtils.asMap(
                "resource_type", "video"));

        String result = (String) deleteResult.get("result");
        if (!"ok".equals(result)) {
            throw new RuntimeException("Delete song failed. Public ID: " + publicId);
        }
    }

    @Override
    public Song Update(Long id, String userId) throws Exception {
        User user = null;
        Optional<User> existingArtist = userRepository.findById(Long.parseLong(userId));
        if (existingArtist.isEmpty()) {
            throw new DataNotFoundException("Artist not found.");
        }
        user = existingArtist.get();

        Optional<Song> existingSong = songRepository.findById(id);
        if (existingSong.isEmpty()) {
            throw new DataNotFoundException("Song not found.");
        }
        Song song = existingSong.get();

        if (!user.getId().equals(song.getArtistId())) {
            throw new DataNotFoundException("You are not the owner of this song.");
        }

        song.setStatus(Song.Status.PENDING);

        if (song.getCreatedAt() == null) {
            song.setCreatedAt(LocalDateTime.now());
        }
        song.setUpdatedAt(LocalDateTime.now());

        return songRepository.save(song);
    }
}