package xyz.ztzhome.zblog.entity.Bean;

import lombok.Data;
import java.util.Date;

@Data
public class UserRecentlyPlayed {
    private long id;
    private long userId;
    private long songId;
    private Date playTime;
}

