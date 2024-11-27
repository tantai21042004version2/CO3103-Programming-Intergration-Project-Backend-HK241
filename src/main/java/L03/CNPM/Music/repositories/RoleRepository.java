package L03.CNPM.Music.repositories;

import L03.CNPM.Music.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    @SuppressWarnings("null")
    Optional<Role> findById(Long id);

    Optional<Role> findByName(String name);
}
