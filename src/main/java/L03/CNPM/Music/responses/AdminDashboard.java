package L03.CNPM.Music.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminDashboard {
    @JsonProperty("total_users")
    private int totalUsers;

    @JsonProperty("total_artists")
    private int totalArtists;

    @JsonProperty("total_active_users")
    private int totalActiveUsers;

    @JsonProperty("total_albums")
    private int totalAlbums;

    @JsonProperty("total_approved_albums")
    private int totalApprovedAlbums;

    @JsonProperty("total_pending_albums")
    private int totalPendingAlbums;

    @JsonProperty("total_songs")
    private int totalSongs;

    @JsonProperty("total_approved_songs")
    private int totalApprovedSongs;

    @JsonProperty("total_pending_songs")
    private int totalPendingSongs;
}
