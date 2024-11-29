package L03.CNPM.Music.services.playlist;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import L03.CNPM.Music.DTOS.playlist.ApprovePlalistDTO;
import L03.CNPM.Music.DTOS.playlist.CreatePlayListDTO;
import L03.CNPM.Music.DTOS.playlist.UpdatePlaylistDTO;
import L03.CNPM.Music.DTOS.playlist.UploadSongToPlaylistDTO;
import L03.CNPM.Music.models.Playlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IPlaylistService {
    Map<String, Object> UploadCloudinary(MultipartFile file, Long artistId) throws Exception;

    Playlist Create(CreatePlayListDTO createPlaylistDTO, Long artistId) throws Exception;

    Playlist AddSong(UploadSongToPlaylistDTO uploadSongToPlaylistDTO, Long playlistId, Long artistId) throws Exception;

    Playlist SubmitPlaylist(Long playlistId, Long artistId) throws Exception;

    Playlist Detail(Long playlistId) throws Exception;

    Playlist Approve(Long playlistId, ApprovePlalistDTO approvePlalistDTO) throws Exception;

    Playlist Update(Long playlistId, UpdatePlaylistDTO updatePlaylistDTO) throws Exception;

    Page<Playlist> Get(String keyword, Pageable pageable);

    Page<Playlist> GetPending(String keyword, Pageable pageable);

    void Delete(Long playlistId) throws Exception;
}
