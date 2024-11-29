package L03.CNPM.Music.repositories;

import L03.CNPM.Music.models.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    @SuppressWarnings("null")
    Optional<Playlist> findById(Long id);

    @Query("SELECT p FROM Playlist p WHERE p.status = 'APPROVED' AND p.name LIKE %?1%")
    Page<Playlist> get(String keyword, Pageable pageable);

    @Query("SELECT p FROM Playlist p WHERE p.status = 'PENDING' AND p.name LIKE %?1%")
    Page<Playlist> getPending(String keyword, Pageable pageable);

    Optional<Playlist> findByName(String name);
}
