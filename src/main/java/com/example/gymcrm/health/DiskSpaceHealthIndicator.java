package com.example.gymcrm.health;

import java.io.File;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class DiskSpaceHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        File file = new File("/");
        long freeSpace = file.getFreeSpace();
        long totalSpace = file.getTotalSpace();
        long usableSpace = file.getUsableSpace();
        return Health.up().withDetail("freeSpace", freeSpace / 1024 / 1024 + " MB").withDetail("totalSpace", totalSpace / 1024 / 1024 + " MB").withDetail("usableSpace", usableSpace / 1024 / 1024 + " MB").build();
    }

}
