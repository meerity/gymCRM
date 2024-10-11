package com.example.gymcrm.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.gymcrm.dto.response.trainingtype.TrainingTypeResponse;
import com.example.gymcrm.service.TrainingTypeService;

class TrainingTypeControllerTest {

    @Mock
    private TrainingTypeService trainingTypeService;

    @InjectMocks
    private TrainingTypeController trainingTypeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTrainingTypes() {
        TrainingTypeResponse trainingTypeResponse1 = new TrainingTypeResponse();
        trainingTypeResponse1.setTypeName("Strength");
        trainingTypeResponse1.setId(1);
        TrainingTypeResponse trainingTypeResponse2 = new TrainingTypeResponse();
        trainingTypeResponse2.setTypeName("Cardio");
        trainingTypeResponse2.setId(2);
        List<TrainingTypeResponse> mockResponse = Arrays.asList(
            trainingTypeResponse1,
            trainingTypeResponse2
        );
        when(trainingTypeService.getTrainingTypes()).thenReturn(mockResponse);

        ResponseEntity<List<TrainingTypeResponse>> response = trainingTypeController.getTrainingTypes();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }
}
