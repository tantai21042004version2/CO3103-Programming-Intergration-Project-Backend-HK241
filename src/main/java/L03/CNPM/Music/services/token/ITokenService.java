package L03.CNPM.Music.services.token;

import L03.CNPM.Music.models.Token;
import L03.CNPM.Music.models.User;

public interface ITokenService {
    Token addToken(User user, String token, boolean isMobileDevice);
}
