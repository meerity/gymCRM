package com.example.gymcrm.repository;

import com.example.gymcrm.entity.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee, Integer> {
    


}
