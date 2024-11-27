package L03.CNPM.Music.repositories;

import L03.CNPM.Music.models.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    @SuppressWarnings("null")
    Optional<Playlist> findById(Long id);

    Optional<Playlist> findByName(String name);
}
