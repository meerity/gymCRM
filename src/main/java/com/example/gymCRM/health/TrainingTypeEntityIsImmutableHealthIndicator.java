package com.example.gymcrm.health;

import java.util.Optional;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import com.example.gymcrm.entity.TrainingType;
import com.example.gymcrm.repository.TrainingTypeRepository;

@Component
@RequiredArgsConstructor
public class TrainingTypeEntityIsImmutableHealthIndicator implements HealthIndicator {

    private final TrainingTypeRepository trainingTypeRepository;

    @Override
    public Health health() {
        Optional<TrainingType> trainingType1 = trainingTypeRepository.findById(1);
        Optional<TrainingType> trainingType2 = trainingTypeRepository.findById(2);
        if (trainingType1.get().getTypeName().equals("Strength") && 
            trainingType2.get().getTypeName().equals("Cardio")) {
            return Health.up().withDetail("CustomDBIndicator", "TrainingType Entity has not been changed").build();
        } else {
            return Health.down().withDetail("CustomDBIndicator", "TrainingType Entity has changed!").build();
        }
    }
}

