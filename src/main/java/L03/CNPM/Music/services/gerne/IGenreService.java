package L03.CNPM.Music.services.gerne;

import L03.CNPM.Music.models.Genre;

public interface IGenreService {
    Genre Detail(Long id) throws Exception;
}
