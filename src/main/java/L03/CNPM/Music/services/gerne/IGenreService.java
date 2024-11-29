package L03.CNPM.Music.services.gerne;

import L03.CNPM.Music.models.Genre;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IGenreService {
    List<Genre> Get(Pageable pageable, String keyword);

    List<Genre> GetAll();

    Genre Detail(Long id) throws Exception;
}
