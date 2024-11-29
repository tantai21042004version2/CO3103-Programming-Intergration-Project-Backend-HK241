package L03.CNPM.Music.repositories;

import L03.CNPM.Music.models.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    @Query("SELECT g FROM Genre g WHERE g.name LIKE %?1%")
    List<Genre> findAll(Pageable pageable, String keyword);

    @SuppressWarnings("null")
    Optional<Genre> findById(Long id);

    List<Genre> findByNameIn(List<String> genreNames);

    List<Genre> findGenresByIdIn(List<Long> genreIds);
}
