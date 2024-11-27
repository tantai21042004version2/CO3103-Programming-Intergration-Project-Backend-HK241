package L03.CNPM.Music.utils;

import L03.CNPM.Music.exceptions.DataNotFoundException;


public class RepositoryUtils {
    public static <T> T findOrThrow(T value, String message) throws DataNotFoundException {
        if (value == null) {
            throw new DataNotFoundException(message);
        }
        return value;
    }
}
