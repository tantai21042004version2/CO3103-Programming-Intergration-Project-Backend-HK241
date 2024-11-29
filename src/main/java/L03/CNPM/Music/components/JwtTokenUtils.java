package L03.CNPM.Music.components;

import L03.CNPM.Music.exceptions.InvalidParamException;
import L03.CNPM.Music.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {
    @Value("${jwt.expiration}")
    private int expiration;

    @Value("${jwt.expiration-refresh-token}")
    private int expirationRefreshToken;

    @Value("${jwt.secretKey}")
    private String secretKey;

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtils.class);

    @SuppressWarnings("deprecation")
    public String generateToken(User user) throws Exception {
        Map<String, Object> claims = new HashMap<>();

        String subject = user.getUsername();
        claims.put("subject", subject);
        claims.put("userId", String.valueOf(user.getId()));
        claims.put("role", user.getRole().getName());

        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);

        long expirationTimeInMillis = 2 * 60 * 60 * 1000L;
        Date expirationDate = new Date(now + expirationTimeInMillis);

        try {
            String token = Jwts.builder()
                    .claims(claims)
                    .setIssuedAt(issuedAt)
                    .setExpiration(expirationDate)
                    .signWith(getSignInKey(), Jwts.SIG.HS256)
                    .compact();

            return token;
        } catch (Exception e) {
            throw new InvalidParamException("Cannot create jwt token, error: " + e.getMessage());
        }
    }

    private SecretKey getSignInKey() {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getSubject(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("subject", String.class);
    }

    public String getUserId(String token) {
        Claims claims = extractAllClaims(token);

        return claims.get("userId", String.class);
    }

    public boolean isTokenExpired(String token) {
        Claims claims = extractAllClaims(token);

        Date expirationDate = claims.getExpiration();
        return expirationDate.before(new Date());
    }

    public boolean validateToken(String token, User user) {
        try {
            Claims claims = extractAllClaims(token);

            String subject = claims.get("subject", String.class);
            String userId = claims.get("userId", String.class);
            String role = claims.get("role", String.class);

            Date expiration = claims.getExpiration();
            return expiration.after(new Date())
                    && subject.equals(user.getUsername())
                    && userId.equals(String.valueOf(user.getId()))
                    && role.equals(user.getRole().getName());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Error validating JWT token: {}", e.getMessage());
        }
        return false;
    }
}
