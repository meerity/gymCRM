package com.example.gymcrm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "gym_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "is_active", nullable = false, unique = true)
    private boolean active;

    @OneToOne(mappedBy = "user")
    private Trainer trainer;

    @OneToOne(mappedBy = "user")
    private Trainee trainee;

    public boolean getActive(){
        return active;
    }

    @Override
    public String toString() {
        String activeness = active ? "Active" : "Inactive";
        return ", " + firstName + " " + lastName + ", Username: " + username + ", Status: " + activeness;
    }



}
