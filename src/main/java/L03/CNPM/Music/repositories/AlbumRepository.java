package L03.CNPM.Music.repositories;

import L03.CNPM.Music.models.Album;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
        @SuppressWarnings("null")
        @Query("SELECT a FROM Album a WHERE a.deletedAt IS NULL AND a.id = :albumId")
        Optional<Album> findById(Long albumId);

        @Query("SELECT a FROM Album a WHERE a.artistId = :artistId " +
                        "AND (:nameKeyword IS NULL OR a.name LIKE %:nameKeyword%) " +
                        "AND (:status IS NULL OR a.status = :status) " +
                        "AND (a.deletedAt IS NULL)")
        Page<Album> findAllByArtistId(
                        @Param("artistId") Long artistId,
                        @Param("nameKeyword") String nameKeyword,
                        @Param("status") Album.Status status,
                        Pageable pageable);

        @Query("SELECT a FROM Album a WHERE " +
                        "(:nameKeyword IS NULL OR a.name LIKE %:nameKeyword%) " +
                        "AND (:status IS NULL OR a.status = :status) " +
                        "AND (a.deletedAt IS NULL)")
        Page<Album> get(
                        @Param("nameKeyword") String nameKeyword,
                        @Param("status") Album.Status status,
                        Pageable pageable);

        @Query("SELECT a FROM Album a WHERE a.deletedAt IS NULL " +
                        "AND (:nameKeyword IS NULL OR a.name LIKE %:nameKeyword%)")
        Page<Album> findAll(
                        @Param("nameKeyword") String nameKeyword,
                        Pageable pageable);

        @Query("SELECT COUNT(a) FROM Album a WHERE a.status = :status")
        int countAllByStatus(@Param("status") Album.Status status);

        @Query("SELECT COUNT(a) FROM Album a WHERE a.status = 'PENDING'")
        int countAllPending();
}
