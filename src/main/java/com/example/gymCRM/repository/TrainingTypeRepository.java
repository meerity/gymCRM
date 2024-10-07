package com.example.gymcrm.repository;

import java.util.Optional;
import com.example.gymcrm.entity.TrainingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingTypeRepository extends JpaRepository<TrainingType, Integer>{

    Optional<TrainingType> findByTypeName(String typeName);
}
