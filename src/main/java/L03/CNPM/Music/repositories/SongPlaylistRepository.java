package L03.CNPM.Music.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import L03.CNPM.Music.models.SongPlaylist;
import java.util.Optional;
import java.util.List;

@Repository
public interface SongPlaylistRepository extends JpaRepository<SongPlaylist, Long> {
    Optional<SongPlaylist> findBySongIdAndPlaylistId(Long songId, Long playlistId);

    List<SongPlaylist> findByPlaylistId(Long playlistId);

    Optional<SongPlaylist> findBySongId(Long songId);

    @SuppressWarnings("null")
    void delete(SongPlaylist songPlaylist);
}
