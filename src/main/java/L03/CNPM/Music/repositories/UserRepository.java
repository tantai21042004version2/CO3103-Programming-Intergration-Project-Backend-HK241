package L03.CNPM.Music.repositories;

import L03.CNPM.Music.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT o FROM User o WHERE o.isActive = true AND (:keyword IS NULL OR :keyword = '' OR " +
            "o.username LIKE %:keyword% " +
            "OR o.email LIKE %:keyword% " +
            "OR o.country LIKE %:keyword%) ")
    Page<User> findAll(@Param("keyword") String keyword, Pageable pageable);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    @SuppressWarnings("null")
    Optional<User> findById(Long id);

    boolean existsByUsername(String username);
}
