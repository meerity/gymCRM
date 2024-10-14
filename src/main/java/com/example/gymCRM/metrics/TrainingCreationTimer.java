package com.example.gymcrm.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import org.springframework.stereotype.Component;

@Component
public class TrainingCreationTimer {

    private final Timer creationTimer;

    public TrainingCreationTimer(MeterRegistry meterRegistry) {
        this.creationTimer = Timer.builder("trainings.creation.time")
                .description("Time taken to create a training")
                .register(meterRegistry);
    }

    public void recordCreationTime(Runnable task) {
        creationTimer.record(task);
    }

}