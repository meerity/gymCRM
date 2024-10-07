package com.example.gymcrm.entity;

import lombok.*;

import java.time.LocalDate;
import java.util.Set;
import jakarta.persistence.*;


@Getter
@Setter 
@Entity
@Table(name = "trainee")
public class Trainee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column
    private String address;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id",  referencedColumnName = "id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "trainee", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Training> trainings;

    @ManyToMany(mappedBy = "trainees", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Trainer> trainers;

    @Override
    public String toString() {
        return "Id: " + user + user.toString() + ", Date of birth: " + dateOfBirth + ", Address: " + address;
    }
}
