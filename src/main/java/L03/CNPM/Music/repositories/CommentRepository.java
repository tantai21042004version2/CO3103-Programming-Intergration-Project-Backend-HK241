package L03.CNPM.Music.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import L03.CNPM.Music.models.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c WHERE c.songId = ?1 AND c.deletedAt IS NULL")
    Page<Comment> findAll(Long songId, Pageable pageable);
}
