package com.example.gymcrm.specification;

import com.example.gymcrm.entity.Training;
import com.example.gymcrm.entity.TrainingType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class TrainingSpecifications {

    private TrainingSpecifications() {}

    public static Specification<Training> dateFrom(LocalDate dateFrom) {
        if (dateFrom == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.greaterThanOrEqualTo(root.get("date"), dateFrom);
    }

    public static Specification<Training> dateTo(LocalDate dateTo) {
        if (dateTo == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.lessThanOrEqualTo(root.get("date"), dateTo);
    }

    public static Specification<Training> trainerName(String firstNameLastName) {
        if (firstNameLastName == null) {
            return null;
        }
        return trainerOrTraineeName(firstNameLastName, "trainer");
    }

    public static Specification<Training> traineeName(String firstNameLastName) {
        if (firstNameLastName == null) {
            return null;
        }
        return trainerOrTraineeName(firstNameLastName, "trainee");
    }

    private static Specification<Training> trainerOrTraineeName(String trainerFirstNameLastName, String personType) {
        return (root, query, criteriaBuilder) -> {
            String[] names = trainerFirstNameLastName.split(" ", 2);
            if (names.length == 2) {
                return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get(personType).get("user").get("firstName"), names[0]),
                    criteriaBuilder.equal(root.get(personType).get("user").get("lastName"), names[1])
                );
            } else {
                return criteriaBuilder.or(
                    criteriaBuilder.equal(root.get(personType).get("user").get("firstName"), trainerFirstNameLastName),
                    criteriaBuilder.equal(root.get(personType).get("user").get("lastName"), trainerFirstNameLastName)
                );
            }
        };
    }


    public static Specification<Training> trainingType(TrainingType trainingType) {
        if (trainingType == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("trainingType"), trainingType);
    }
}