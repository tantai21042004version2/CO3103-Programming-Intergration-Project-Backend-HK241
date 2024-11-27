package L03.CNPM.Music.services.album;

import L03.CNPM.Music.DTOS.album.UpdateAlbumDTO;
import L03.CNPM.Music.DTOS.album.UploadAlbumDTO;
import L03.CNPM.Music.DTOS.album.UploadSongToAlbumDTO;
import L03.CNPM.Music.models.Album;
import L03.CNPM.Music.responses.song.SongResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IAlbumService {
    Album uploadAlbum(UploadAlbumDTO uploadAlbumDTO, Long artistId);

    Album Detail(Long albumId) throws Exception;

    List<SongResponse> uploadSongToAlbum(UploadSongToAlbumDTO uploadSongToAlbumDTO, Long albumId) throws Exception;

    Page<Album> findAll(String keyword, Pageable pageable);

    Album updateAlbum(Long albumId, UpdateAlbumDTO updateAlbumDTO) throws Exception;
}
