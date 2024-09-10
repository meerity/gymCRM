package com.example.gymCRM.storage;

import com.example.gymCRM.entity.Trainer;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class TrainerStorage {

    @Getter
    private Map<Integer, Trainer> trainers;
    private final File json;
    private final ObjectMapper mapper = new ObjectMapper();

    public TrainerStorage() {
        json = new File("src/main/resources/trainers.json");
    }

    @PreDestroy
    public void saveChanges() throws IOException {
        mapper.writerWithDefaultPrettyPrinter().writeValue(json, trainers);
    }

    @PostConstruct
    public void init() {
        try {
            trainers = mapper.readValue(json, mapper.getTypeFactory().constructMapType(HashMap.class, Integer.class, Trainer.class));
            log.info("Trainers loaded: {} instances", trainers.size());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}
