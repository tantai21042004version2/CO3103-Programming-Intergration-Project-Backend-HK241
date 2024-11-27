package L03.CNPM.Music.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "playlist")
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "genre_id")
    private Long genreId;

    @Column(name = "cover_url", length = 255)
    private String coverUrl;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('DRAFT','PENDING','APPROVED','REJECTED')")
    private Status status;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;

    public enum Status {
        DRAFT,
        PENDING,
        APPROVED,
        REJECTED
    }
}
