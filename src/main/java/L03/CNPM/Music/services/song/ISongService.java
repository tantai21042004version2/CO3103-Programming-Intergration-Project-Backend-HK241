package L03.CNPM.Music.services.song;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import L03.CNPM.Music.DTOS.song.SongMetadataDTO;
import L03.CNPM.Music.models.Song;

public interface ISongService {
    Page<Song> findAll(String keyword, Pageable pageable);

    Page<Song> findAllPending(String keyword, Pageable pageable);

    Page<Song> findAllByArtistId(String artistId, Pageable pageable);

    Song approveSong(String id) throws Exception;

    Song rejectSong(String id) throws Exception;

    Map<String, Object> uploadSong(MultipartFile file) throws Exception;

    Song createSong(SongMetadataDTO metadataSongDTO) throws Exception;

    void deleteSong(String publicId) throws Exception;

    Song updateSong(String id, String userId) throws Exception;
}