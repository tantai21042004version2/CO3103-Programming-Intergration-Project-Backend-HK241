package L03.CNPM.Music.services.album;

import L03.CNPM.Music.DTOS.album.UpdateAlbumDTO;
import L03.CNPM.Music.DTOS.album.UploadAlbumDTO;
import L03.CNPM.Music.DTOS.album.UploadSongToAlbumDTO;
import L03.CNPM.Music.exceptions.DataNotFoundException;
import L03.CNPM.Music.models.Album;
import L03.CNPM.Music.models.Genre;
import L03.CNPM.Music.models.Song;
import L03.CNPM.Music.repositories.AlbumRepository;
import L03.CNPM.Music.repositories.GenreRepository;
import L03.CNPM.Music.repositories.SongRepository;
import L03.CNPM.Music.responses.song.SongResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumService implements IAlbumService {
    private final AlbumRepository albumRepository;
    private final SongRepository songRepository;
    private final GenreRepository genreRepository;

    @Override
    public Album uploadAlbum(UploadAlbumDTO uploadAlbumDTO, Long artistId) {
        List<Long> genre_id = (uploadAlbumDTO.getGenre_Id()).stream().map(Long::valueOf).toList();
        List<Genre> genreList = genreRepository.findGenresByIdIn(genre_id);
        Album album = Album.builder()
                .name(uploadAlbumDTO.getName())
                .description(uploadAlbumDTO.getDescription())
                .status(Album.Status.DRAFT)
                .artistId(artistId)
                .coverUrl(uploadAlbumDTO.getCoverImageUrl())
                .releaseDate(uploadAlbumDTO.getReleaseDate())
                .createdAt(LocalDate.now().format(DateTimeFormatter.ISO_DATE))
                .updatedAt(LocalDate.now().format(DateTimeFormatter.ISO_DATE))
                .genres(genreList)
                .build();
        albumRepository.save(album);
        return album;
    }

    @Override
    public List<SongResponse> uploadSongToAlbum(UploadSongToAlbumDTO uploadSongToAlbumDTO, Long albumId)
            throws DataNotFoundException {

        if (!albumRepository.existsById(albumId))
            throw new DataNotFoundException("Album with ID %s no found".formatted(albumId));

        List<Song> songs = new ArrayList<>();
        for (Long songId : uploadSongToAlbumDTO.getSongIds()) {
            Optional<Song> existedSong = songRepository.findById(songId);
            if (existedSong.isEmpty()) {
                throw new DataNotFoundException("Song with ID %s not found".formatted(songId));
            }
            Song song = existedSong.get();
            songs.add(song);
            song.setAlbumId(albumId);
            songRepository.save(song);
        }
        return songs.stream().map(SongResponse::fromSong).toList();
    }

    @Override
    public Page<Album> findAll(String keyword, Pageable pageable) {
        if (keyword != null) {
            keyword = keyword.trim();
            if (keyword.isEmpty()) {
                keyword = null;
            }
        }
        return albumRepository.findAll(keyword, pageable);
    }

    @Override
    public Album updateAlbum(Long albumId, UpdateAlbumDTO updateAlbumDTO) throws DataNotFoundException {
        Optional<Album> exitedAlbum = albumRepository.findById(albumId);
        if (exitedAlbum.isEmpty())
            throw new DataNotFoundException("Album with ID %s no found".formatted(albumId));
        Album album = exitedAlbum.get();

        if (updateAlbumDTO.getName() != null)
            album.setName(updateAlbumDTO.getName());
        if (updateAlbumDTO.getDescription() != null)
            album.setDescription(updateAlbumDTO.getDescription());

        verifyListSongId(updateAlbumDTO.getAddList());
        verifyListSongId(updateAlbumDTO.getDeleteList());

        Map<Long, Song> addListSongs = songRepository.findAllById(updateAlbumDTO.getAddList())
                .stream()
                .collect(Collectors.toMap(Song::getId, song -> song));

        for (Long songId : updateAlbumDTO.getAddList()) {
            Song song = addListSongs.get(songId);
            if (song != null) {
                song.setAlbumId(albumId);
                songRepository.save(song);
            }
        }

        Map<Long, Song> deleteListSongs = songRepository.findAllById(updateAlbumDTO.getDeleteList())
                .stream()
                .collect(Collectors.toMap(Song::getId, song -> song));

        for (Long songId : updateAlbumDTO.getDeleteList()) {
            Song song = deleteListSongs.get(songId);
            if (song != null) {
                song.setAlbumId(null);
                songRepository.save(song);
            }
        }
        return album;

    }

    public void verifyListSongId(List<Long> songIdList) throws DataNotFoundException {
        for (Long songId : songIdList) {
            Optional<Song> existedSong = songRepository.findById(songId);
            if (existedSong.isEmpty()) {
                throw new DataNotFoundException("Song with ID %s not found".formatted(songId));
            }
        }
    }

    @Override
    public Album Detail(Long albumId) throws DataNotFoundException {
        return albumRepository.findById(albumId)
                .orElseThrow(() -> new DataNotFoundException("Album with ID %s no found".formatted(albumId)));
    }
}