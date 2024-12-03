package L03.CNPM.Music.services.song;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import L03.CNPM.Music.DTOS.song.SongMetadataDTO;
import L03.CNPM.Music.models.Song;

public interface ISongService {
    Page<Song> Get(String keyword, Pageable pageable);

    Page<Song> GetPending(String keyword, Pageable pageable);

    Page<Song> GetByArtirstId(String artistId, Pageable pageable);

    List<Song> GetByAlbumtId(Long albumId);

    List<Song> GetByPlaylistId(Long playlistId);

    Song ApproveSong(String id) throws Exception;

    Song RejectSong(String id) throws Exception;

    Song Detail(String id) throws Exception;

    Map<String, Object> UploadSong(MultipartFile file) throws Exception;

    Song Create(SongMetadataDTO metadataSongDTO) throws Exception;

    void Delete(String publicId) throws Exception;

    Song Update(String id, String userId) throws Exception;
}