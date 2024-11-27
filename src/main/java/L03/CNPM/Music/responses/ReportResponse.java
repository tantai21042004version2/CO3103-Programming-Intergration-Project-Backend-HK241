package L03.CNPM.Music.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportResponse {
    @JsonProperty("name")
    private String name;

    @JsonProperty("reason")
    private String reason;

    @JsonProperty("report_status")
    private String report_status;

    @JsonProperty("report_date")
    private Date reportDate;

}