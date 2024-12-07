package L03.CNPM.Music.configurations;

import L03.CNPM.Music.components.JwtTokenFilter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import java.util.List;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity(debug = true)
@EnableWebMvc
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration {
    private final JwtTokenFilter jwtTokenFilter;
    @Value("${api.prefix}")
    private String apiPrefix;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(customCORS()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                String.format("%s/users/register", apiPrefix),
                                String.format("%s/users/login", apiPrefix),
                                String.format("%s/roles", apiPrefix),
                                String.format("%s/songs", apiPrefix),
                                String.format("%s/genres", apiPrefix),
                                String.format("%s/genres/list", apiPrefix),
                                String.format("%s/albums/list", apiPrefix),
                                String.format("%s/albums/detail/*", apiPrefix),
                                String.format("%s/playlists/list", apiPrefix),
                                String.format("%s/playlists/detail/*", apiPrefix),
                                String.format("%s/comments/list/*", apiPrefix),
                                String.format("%s/songs/detail/*", apiPrefix))
                        .permitAll()
                        .anyRequest().authenticated());
        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource customCORS() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:4200",
                "https://co3103-programming-integration-project-55ev.onrender.com"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
