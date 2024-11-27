package L03.CNPM.Music.repositories;

import L03.CNPM.Music.models.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    @SuppressWarnings("null")
    Optional<Genre> findById(Long id);

    List<Genre> findByNameIn(List<String> genreNames);

    List<Genre> findGenresByIdIn(List<Long> genreIds);
}
