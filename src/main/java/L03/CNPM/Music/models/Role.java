package L03.CNPM.Music.models;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    public static String ADMIN = "ADMIN";

    public static String LISTENER = "LISTENER";

    public static String ARTIST = "ARTIST";
}