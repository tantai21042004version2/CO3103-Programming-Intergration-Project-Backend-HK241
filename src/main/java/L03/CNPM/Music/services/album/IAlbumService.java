package L03.CNPM.Music.services.album;

import L03.CNPM.Music.DTOS.album.ApproveAlbumDTO;
import L03.CNPM.Music.DTOS.album.UploadAlbumDTO;
import L03.CNPM.Music.DTOS.album.UploadSongToAlbumDTO;
import L03.CNPM.Music.models.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface IAlbumService {
    Map<String, Object> AdminDashboard() throws Exception;

    Map<String, Object> UploadCloudinary(MultipartFile file, Long artistId) throws Exception;

    Album Create(UploadAlbumDTO uploadAlbumDTO, Long artistId) throws Exception;

    Album AddSong(UploadSongToAlbumDTO uploadSongToAlbumDTO, Long albumId, Long artistId) throws Exception;

    Album SubmitAlbum(Long albumId, Long artistId) throws Exception;

    Album Detail(Long albumId) throws Exception;

    Album Approve(Long albumId, ApproveAlbumDTO approveAlbumDTO) throws Exception;

    Page<Album> GetByArtistId(Long artistId, Pageable pageable, String keyword, Album.Status status);

    Page<Album> Get(String keyword, Album.Status status, Pageable pageable);

    void Delete(Long albumId) throws Exception;
}
