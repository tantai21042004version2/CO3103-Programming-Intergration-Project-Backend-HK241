package L03.CNPM.Music.repositories;

import L03.CNPM.Music.models.Song;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    @Query("SELECT s FROM Song s WHERE (:keyword IS NULL OR s.name LIKE %:keyword%) AND (:albumId IS NULL OR s.albumId = :albumId) AND s.status = 'APPROVED' ORDER BY s.id DESC")
    Page<Song> findAll(@Param("keyword") String keyword, @Param("albumId") Long albumId, Pageable pageable);

    @Query("SELECT s FROM Song s WHERE s.artistId = :artistId")
    Page<Song> findAllByArtistId(@Param("artistId") Long artistId, Pageable pageable);

    @Query("SELECT s FROM Song s WHERE (:keyword IS NULL OR s.name LIKE %:keyword%) AND s.status = 'PENDING' ORDER BY s.id DESC")
    Page<Song> findAllPending(@Param("keyword") String keyword, Pageable pageable);

    @SuppressWarnings("null")
    Optional<Song> findById(Long songId);

    @SuppressWarnings("null")
    List<Song> findAllByAlbumId(Long albumId);

    boolean existsById(@SuppressWarnings("null") Long albumId);

    @Query("SELECT COUNT(s) FROM Song s WHERE s.status = :status")
    int countAllByStatus(@Param("status") Song.Status status);

    @Query("SELECT COUNT(s) FROM Song s WHERE s.status = 'PENDING'")
    int countAllPending();
}
