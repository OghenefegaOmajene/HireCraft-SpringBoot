package HireCraft.com.SpringBoot.utils;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class TimeAgoUtil {

    public String format(LocalDateTime createdAt) {
        Duration duration = Duration.between(createdAt, LocalDateTime.now());

        long seconds = duration.getSeconds();

        if (seconds < 60) return seconds + " seconds ago";
        if (seconds < 3600) return seconds / 60 + " minutes ago";
        if (seconds < 86400) return seconds / 3600 + " hours ago";
        if (seconds < 2592000) return seconds / 86400 + " days ago";
        if (seconds < 31536000) return seconds / 2592000 + " months ago";
        return seconds / 31536000 + " years ago";
    }
}
