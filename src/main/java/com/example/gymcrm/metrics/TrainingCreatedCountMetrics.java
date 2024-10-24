package com.example.gymcrm.metrics;

import org.springframework.stereotype.Component;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;

@Component
public class TrainingCreatedCountMetrics {

    private final Counter trainingCreatedCount;

    public TrainingCreatedCountMetrics(MeterRegistry meterRegistry) {
        this.trainingCreatedCount = Counter.builder("trainings.created.count")
                .description("Number of trainings created")
                .register(meterRegistry);
    }

    public void increment() {
        trainingCreatedCount.increment();
    }

}
