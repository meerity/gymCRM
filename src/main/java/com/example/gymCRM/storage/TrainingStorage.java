package com.example.gymCRM.storage;

import com.example.gymCRM.entity.Training;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
public class TrainingStorage {

    @Getter
    private Map<String, Training> trainings;
    private final File json;
    private final ObjectMapper mapper;

    public TrainingStorage() {
        json = new File("src/main/resources/trainings.json");
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    @PreDestroy
    public void saveChanges() throws IOException {
        mapper.writerWithDefaultPrettyPrinter().writeValue(json, trainings);
    }

    @PostConstruct
    public void init() {
        try {
            trainings = mapper.readValue(json, mapper.getTypeFactory().constructMapType(HashMap.class, String.class, Training.class));
            if (trainings == null){
                log.error("trainings is null");
            } else {
                log.info("Trainings loaded: {} instances", trainings.size());
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}
