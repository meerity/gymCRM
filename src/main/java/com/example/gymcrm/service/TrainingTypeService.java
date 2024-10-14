package com.example.gymcrm.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.example.gymcrm.dto.response.trainingtype.TrainingTypeResponse;
import com.example.gymcrm.repository.TrainingTypeRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrainingTypeService {
    
    private final TrainingTypeRepository trainingTypeRepository;

    public List<TrainingTypeResponse> getTrainingTypes() {
        return trainingTypeRepository.findAll().stream().map(trainingType -> {
            TrainingTypeResponse trainingTypeResponse = new TrainingTypeResponse();
            trainingTypeResponse.setTypeName(trainingType.getTypeName());
            trainingTypeResponse.setId(trainingType.getId());
            return trainingTypeResponse;
        }).toList();
    }
}
