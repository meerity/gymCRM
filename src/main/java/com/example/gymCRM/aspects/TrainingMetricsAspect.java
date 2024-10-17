package com.example.gymcrm.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import com.example.gymcrm.metrics.TrainingCreatedCountMetrics;
import com.example.gymcrm.metrics.TrainingCreationTimer;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class TrainingMetricsAspect {

    private final TrainingCreatedCountMetrics trainingCreatedCountMetrics;
    private final TrainingCreationTimer trainingCreationTimer;

    @Pointcut("execution(* com.example.gymcrm.service.TrainingService.addTraining(..))")
    public void addTrainingConfig() {}

    @AfterReturning("addTrainingConfig()")
    public void afterAddTraining() {
        trainingCreatedCountMetrics.increment();
    }

    @Around("addTrainingConfig()")
    public void aroundAddTraining(ProceedingJoinPoint joinPoint) throws Throwable {
        trainingCreationTimer.recordCreationTime(() -> {
            try {
                joinPoint.proceed();
            } catch (Throwable e) {
                log.error("Error during training creation", e);
            }
        });
    }
        
}
