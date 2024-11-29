package L03.CNPM.Music.services.gerne;

import java.util.Optional;

import org.springframework.stereotype.Service;

import L03.CNPM.Music.repositories.GenreRepository;
import L03.CNPM.Music.exceptions.DataNotFoundException;
import L03.CNPM.Music.models.Genre;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService implements IGenreService {
    private final GenreRepository genreRepository;

    @Override
    public List<Genre> Get(Pageable pageable, String keyword) {
        return genreRepository.findAll(pageable, keyword);
    }

    @Override
    public List<Genre> GetAll() {
        return genreRepository.findAll();
    }

    @Override
    public Genre Detail(Long id) throws Exception {
        Optional<Genre> genre = genreRepository.findById(id);
        if (genre.isEmpty()) {
            throw new DataNotFoundException("Genre not found");
        }
        return genre.get();
    }
}
