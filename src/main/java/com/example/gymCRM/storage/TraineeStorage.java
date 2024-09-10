package com.example.gymCRM.storage;

import com.example.gymCRM.entity.Trainee;
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
public class TraineeStorage {

    @Getter
    private Map<Integer, Trainee> trainees;
    private final File json;
    private final ObjectMapper mapper;

    public TraineeStorage() {
        json = new File("src/main/resources/trainees.json");
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    @PreDestroy
    public void saveChanges() throws IOException {
        mapper.writerWithDefaultPrettyPrinter().writeValue(json, trainees);
    }

    @PostConstruct
    public void init() {
        try {
            trainees = mapper.readValue(json, mapper.getTypeFactory().constructMapType(HashMap.class, Integer.class, Trainee.class));
            log.info("Trainees loaded: {} instances", trainees.size());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
