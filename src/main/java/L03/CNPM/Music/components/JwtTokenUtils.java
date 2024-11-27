package L03.CNPM.Music.components;

import L03.CNPM.Music.exceptions.InvalidParamException;
import L03.CNPM.Music.models.Token;
import L03.CNPM.Music.models.User;
import L03.CNPM.Music.repositories.TokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {
    @Value("${jwt.expiration}")
    private int expiration;

    @Value("${jwt.expiration-refresh-token}")
    private int expirationRefreshToken;

    @Value("${jwt.secretKey}")
    private String secretKey;

    private final TokenRepository tokenRepository;
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtils.class);

    public String generateToken(User user) throws Exception {
        Map<String, Object> claims = new HashMap<>();

        String subject = getSubject(user);
        claims.put("subject", subject);
        claims.put("userId", String.valueOf(user.getId()));
        claims.put("role", user.getRole().getName());

        try {
            String token = Jwts.builder()
                    .claims(claims)
                    .subject(subject)
                    .expiration(new Date(System.currentTimeMillis() + expiration * 1000L))
                    .signWith(getSignInKey(), Jwts.SIG.HS256)
                    .compact();
            return token;
        } catch (Exception e) {
            throw new InvalidParamException("Cannot create jwt token, error: " + e.getMessage());
        }
    }

    private String getSubject(User user) {
        return user.getUsername();
    }

    private SecretKey getSignInKey() {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        // Keys.hmacShaKeyFor(Decoders.BASE64.decode("TaqlmGv1iEDMRiFp/pHuID1+T84IABfuA0xXh4GhiUI="));
        return Keys.hmacShaKeyFor(bytes);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser() // Khởi tạo JwtParserBuilder
                .verifyWith(getSignInKey()) // Sử dụng verifyWith() để thiết lập signing key
                .build() // Xây dựng JwtParser
                .parseSignedClaims(token) // Phân tích token đã ký
                .getPayload(); // Lấy phần body của JWT, chứa claims
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String getSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String getUserId(String token) {
        return extractClaim(token, claims -> {
            Object userId = claims.get("userId");
            if (userId instanceof Integer) {
                return String.valueOf(userId);
            }
            if (userId instanceof String) {
                return (String) userId;
            }
            throw new IllegalArgumentException("Unexpected type for userId claim: " + userId.getClass().getName());
        });
    }

    // check expiration
    public boolean isTokenExpired(String token) {
        Date expirationDate = this.extractClaim(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }

    public boolean validateToken(String token, User userDetails) {
        try {
            String subject = extractClaim(token, Claims::getSubject);
            // subject is phoneNumber or email
            Token existingToken = tokenRepository.findByToken(token);
            if (existingToken == null ||
                    existingToken.isRevoked() ||
                    !userDetails.isActive()) {
                return false;
            }
            return (subject.equals(userDetails.getUsername()))
                    && !isTokenExpired(token);
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}
