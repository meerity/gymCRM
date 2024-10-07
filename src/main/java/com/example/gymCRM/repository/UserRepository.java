package com.example.gymcrm.repository;

import java.util.List;
import java.util.Optional;
import com.example.gymcrm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{

    List<User> findByFirstNameAndLastName(String firstName, String lastName);

    Optional<User> findByUsername(String username);

    List<User> findAllByUsernameIn(List<String> usernames);

}
