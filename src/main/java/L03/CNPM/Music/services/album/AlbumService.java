package L03.CNPM.Music.services.album;

import L03.CNPM.Music.DTOS.album.ApproveAlbumDTO;
import L03.CNPM.Music.DTOS.album.UploadAlbumDTO;
import L03.CNPM.Music.DTOS.album.UploadSongToAlbumDTO;
import L03.CNPM.Music.components.LocalizationUtils;
import L03.CNPM.Music.exceptions.DataNotFoundException;
import L03.CNPM.Music.exceptions.UploadCloudinaryException;
import L03.CNPM.Music.models.Album;
import L03.CNPM.Music.models.Genre;
import L03.CNPM.Music.models.Role;
import L03.CNPM.Music.models.Song;
import L03.CNPM.Music.models.User;
import L03.CNPM.Music.repositories.AlbumRepository;
import L03.CNPM.Music.repositories.GenreRepository;
import L03.CNPM.Music.repositories.SongRepository;
import L03.CNPM.Music.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import L03.CNPM.Music.repositories.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import com.cloudinary.Cloudinary;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.utils.ObjectUtils;

@Service
@RequiredArgsConstructor
public class AlbumService implements IAlbumService {
    private final AlbumRepository albumRepository;
    private final SongRepository songRepository;
    private final GenreRepository genreRepository;
    private final UserRepository userRepository;
    private final LocalizationUtils localizationUtils;
    private final Cloudinary cloudinary;

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> UploadCloudinary(MultipartFile file, Long artistId) throws Exception {
        Optional<User> existedArtist = userRepository.findById(artistId);
        if (existedArtist.isEmpty()) {
            throw new DataNotFoundException("Artist with ID %s not found".formatted(artistId));
        }

        if (!existedArtist.get().getRole().getName().equals(Role.ARTIST)) {
            throw new DataNotFoundException("User with ID %s is not an artist".formatted(artistId));
        }

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File does not exist or is empty.");
        }

        Map<String, Object> response;
        Map<String, Object> uploadParams = ObjectUtils.asMap(
                "resource_type", "image",
                "folder", "albums");

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
    public Album Create(UploadAlbumDTO uploadAlbumDTO, Long artistId) throws Exception {
        Optional<Genre> existedGenre = genreRepository.findById(uploadAlbumDTO.getGenreId());
        if (existedGenre.isEmpty()) {
            throw new DataNotFoundException("Genre with ID %s not found".formatted(uploadAlbumDTO.getGenreId()));
        }

        Optional<User> existedArtist = userRepository.findById(artistId);
        if (existedArtist.isEmpty()) {
            throw new DataNotFoundException("Artist with ID %s not found".formatted(artistId));
        }

        Album album = Album.builder()
                .name(uploadAlbumDTO.getName())
                .artistId(artistId)
                .releaseDate(LocalDate.parse(uploadAlbumDTO.getReleaseDate()))
                .coverUrl(uploadAlbumDTO.getCoverUrl())
                .description(uploadAlbumDTO.getDescription())
                .status(Album.Status.DRAFT)
                .genreId(uploadAlbumDTO.getGenreId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return albumRepository.save(album);
    }

    @Override
    public Album AddSong(UploadSongToAlbumDTO uploadSongToAlbumDTO, Long albumId, Long artistId)
            throws Exception {
        Optional<Album> exitedAlbum = albumRepository.findById(albumId);
        if (exitedAlbum.isEmpty())
            throw new DataNotFoundException("Album with ID %s no found".formatted(albumId));
        Album album = exitedAlbum.get();

        Optional<User> existingArtist = userRepository.findById(artistId);
        if (existingArtist.isEmpty())
            throw new DataNotFoundException("Artist with ID %s not found".formatted(artistId));
        User artist = existingArtist.get();

        if (!album.getArtistId().equals(artistId))
            throw new DataNotFoundException("Artist with ID %s is not the owner of the album".formatted(artistId));

        for (Long songId : uploadSongToAlbumDTO.getAddSongIds()) {
            Optional<Song> existedSong = songRepository.findById(songId);
            if (existedSong.isEmpty())
                throw new DataNotFoundException("Song with ID %s not found".formatted(songId));
            Song song = existedSong.get();
            if (song.getAlbumId() != null)
                throw new DataNotFoundException("Song with ID %s already in another album".formatted(songId));

            if (!song.getArtistId().equals(artistId))
                throw new DataNotFoundException(
                        "Song with ID %s is not owned by artist with ID %s".formatted(songId, artistId));

            song.setAlbumId(albumId);
            if (album.getCoverUrl() != null)
                song.setImageUrl(album.getCoverUrl());
            song.setUpdatedAt(LocalDateTime.now());
            songRepository.save(song);
        }

        for (Long songId : uploadSongToAlbumDTO.getRemoveSongIds()) {
            Optional<Song> existedSong = songRepository.findById(songId);
            if (existedSong.isEmpty())
                throw new DataNotFoundException("Song with ID %s not found".formatted(songId));
            Song song = existedSong.get();
            if (!song.getAlbumId().equals(albumId))
                throw new DataNotFoundException(
                        "Song with ID %s is not in album with ID %s".formatted(songId, albumId));

            song.setAlbumId(null);
            if (artist.getImageUrl() != null)
                song.setImageUrl(artist.getImageUrl());
            song.setUpdatedAt(LocalDateTime.now());
            songRepository.save(song);
        }

        if (album.getCreatedAt() == null)
            album.setCreatedAt(LocalDateTime.now());
        album.setUpdatedAt(LocalDateTime.now());

        return albumRepository.save(album);
    }

    @Override
    public Album SubmitAlbum(Long albumId, Long artistId) throws Exception {
        Optional<Album> exitedAlbum = albumRepository.findById(albumId);
        if (exitedAlbum.isEmpty())
            throw new DataNotFoundException("Album with ID %s no found".formatted(albumId));
        Album album = exitedAlbum.get();

        if (!album.getArtistId().equals(artistId))
            throw new DataNotFoundException(
                    "Artist with ID %s is not the owner of the album".formatted(artistId));

        album.setStatus(Album.Status.PENDING);
        album.setUpdatedAt(LocalDateTime.now());

        return albumRepository.save(album);
    }

    @Override
    public Album Detail(Long albumId) throws Exception {
        Optional<Album> exitedAlbum = albumRepository.findById(albumId);
        if (exitedAlbum.isEmpty())
            throw new DataNotFoundException("Album with ID %s no found".formatted(albumId));
        return exitedAlbum.get();
    }

    @Override
    public Album Approve(Long albumId, ApproveAlbumDTO approveAlbumDTO) throws Exception {
        Optional<Album> exitedAlbum = albumRepository.findById(albumId);
        if (exitedAlbum.isEmpty())
            throw new DataNotFoundException("Album with ID %s no found".formatted(albumId));
        Album album = exitedAlbum.get();

        if (approveAlbumDTO.getIsApproved()) {
            album.setStatus(Album.Status.APPROVED);
        } else {
            album.setStatus(Album.Status.REJECTED);
        }

        album.setDescription(approveAlbumDTO.getDescription());
        album.setUpdatedAt(LocalDateTime.now());

        return albumRepository.save(album);
    }

    @Override
    public Page<Album> GetByArtistId(Long artistId, Pageable pageable, String keyword, Album.Status status) {
        if (keyword != null) {
            keyword = keyword.trim();
            if (keyword.isEmpty()) {
                keyword = null;
            }
        }
        return albumRepository.findAllByArtistId(artistId, keyword, status, pageable);
    }

    @Override
    public Page<Album> Get(String keyword, Album.Status status, Pageable pageable) {
        if (keyword != null) {
            keyword = keyword.trim();
            if (keyword.isEmpty()) {
                keyword = null;
            }
        }
        return albumRepository.get(keyword, status, pageable);
    }

    @Override
    public void Delete(Long albumId) throws Exception {
        Optional<Album> exitedAlbum = albumRepository.findById(albumId);
        if (exitedAlbum.isEmpty())
            throw new DataNotFoundException("Album with ID %s no found".formatted(albumId));
        Album album = exitedAlbum.get();
        if (album.getDeletedAt() != null)
            throw new DataNotFoundException("Album with ID %s already deleted".formatted(albumId));

        album.setDeletedAt(LocalDateTime.now());
        albumRepository.save(album);
    }
}