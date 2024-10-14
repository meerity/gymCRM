package com.example.gymcrm.entity;

import java.util.Set;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "training_type")
public class TrainingType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "type_name", nullable = false, unique = true)
    private String typeName;

    @OneToMany(mappedBy = "trainingType")
    private Set<Training> trainings;

    @OneToMany(mappedBy = "specialization")
    private Set<Trainer> trainers;

    @Override
    public String toString() {
        return typeName;
    }

    
}
