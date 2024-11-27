package L03.CNPM.Music.responses;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewResponse {

    private String songName;

    private String comment;

    private int rating;

    private Date reviewDate;

}