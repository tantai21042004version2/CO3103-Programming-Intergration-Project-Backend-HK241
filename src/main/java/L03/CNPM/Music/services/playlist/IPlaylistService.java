package L03.CNPM.Music.services.playlist;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import L03.CNPM.Music.DTOS.playlist.CreatePlayListDTO;
import L03.CNPM.Music.DTOS.playlist.UpdatePlaylistDTO;
import L03.CNPM.Music.models.Playlist;

public interface IPlaylistService {
    Map<String, Object> UploadCloudinary(MultipartFile file) throws Exception;

    Playlist Detail(Long playlistId) throws Exception;

    Playlist Create(CreatePlayListDTO createPlaylistDTO, String userId) throws Exception;

    Playlist Update(Long playlistId, UpdatePlaylistDTO updatePlaylistDTO) throws Exception;
}
