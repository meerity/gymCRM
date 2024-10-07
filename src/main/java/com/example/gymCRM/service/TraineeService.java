package com.example.gymcrm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;
import com.example.gymcrm.dto.TraineeDTO;
import com.example.gymcrm.dto.response.TraineeResponse;
import com.example.gymcrm.dto.update.TraineeUpdateDTO;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.entity.Training;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.repository.TraineeRepository;
import com.example.gymcrm.repository.UserRepository;
import com.example.gymcrm.util.UserUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class TraineeService {

    private final TraineeRepository traineeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String THIS_USER_IS_TRAINER = "This user is trainer!";

    private Pair<User, Trainee> getUserTraineeByUsername(String username) throws NoSuchElementException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User with username " + username + " not found"));
        if (user.getTrainee() == null && user.getTrainer() != null) {
            throw new NoSuchElementException(THIS_USER_IS_TRAINER);
        }
        Trainee trainee = user.getTrainee();
        return Pair.of(user, trainee);
    }

    public List<Trainee> getAllTrainees() {
        return traineeRepository.findAll();
    }
    public Trainee getTraineeById(int id) {
        return traineeRepository.findById(id).orElseThrow();
    }

    public Trainee getTraineeByUsername(String username){
        User user = userRepository.findByUsername(username).orElseThrow();
        if (user.getTrainee() == null && user.getTrainer() != null) {
            throw new NoSuchElementException(THIS_USER_IS_TRAINER);
        } else {
            return user.getTrainee();
        }
    }

    public TraineeResponse getTraineeForResponseByUsername(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Trainee was not found"));
        Trainee trainee = user.getTrainee();

        if (trainee == null) {
            throw new NoSuchElementException(THIS_USER_IS_TRAINER);
        }

        TraineeResponse response = new TraineeResponse();
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setUsername(user.getUsername());
        response.setActive(user.getActive());
        response.setDateOfBirth(trainee.getDateOfBirth());
        response.setAddress(trainee.getAddress());
        response.setTrainerUsernames(trainee.getTrainers().stream().map(trainer -> trainer.getUser().getUsername()).collect(Collectors.toSet()));
        response.setTrainingNames(trainee.getTrainings().stream().map(Training::getTrainingName).collect(Collectors.toSet()));
        return response;
    }

    @Transactional
    public Pair<String, String> createProfile(TraineeDTO traineeDTO) {
        User user = new User();
        user.setFirstName(traineeDTO.getFirstName());
        user.setLastName(traineeDTO.getLastName());
        String username = UserUtils.createUserName(traineeDTO.getFirstName(), traineeDTO.getLastName(), userRepository);
        user.setUsername(username);
        String password = RandomStringUtils.secure().nextAlphanumeric(10);
        user.setPassword(passwordEncoder.encode(password));
        user.setActive(traineeDTO.getIsActive());

        Trainee trainee = new Trainee();
        trainee.setAddress(traineeDTO.getAddress());
        trainee.setDateOfBirth(traineeDTO.getDateOfBirth());
        trainee.setTrainers(new HashSet<>());
        trainee.setTrainings(new HashSet<>());
        trainee.setUser(user);
        user.setTrainee(trainee);

        userRepository.save(user);
        traineeRepository.save(trainee);

        log.info("New trainee profile created: {} {}", user.getFirstName(), user.getLastName());
        return Pair.of(username, password);
    }

    public String updateTrainee(String username, TraineeUpdateDTO traineeUpdateDTO) throws NoSuchElementException {
        Pair<User, Trainee> userAndTrainee = getUserTraineeByUsername(username);
        User user = userAndTrainee.getLeft();
        Trainee trainee = userAndTrainee.getRight();
        
        boolean newFirstnameNotNull = traineeUpdateDTO.getFirstName() != null;
        boolean newLastnameNotNull = traineeUpdateDTO.getLastName() != null;
        String newUsername;

        if (newFirstnameNotNull || newLastnameNotNull) {
            if (newFirstnameNotNull) {
                user.setFirstName(traineeUpdateDTO.getFirstName());
            }
            if (newLastnameNotNull) {
                user.setLastName(traineeUpdateDTO.getLastName());
            }
            newUsername = UserUtils.createUserName(user.getFirstName(), user.getLastName(), userRepository);
            user.setUsername(newUsername);
        } else {
            newUsername = user.getUsername();
        }
        if (traineeUpdateDTO.getDateOfBirth() != null) {
            trainee.setDateOfBirth(traineeUpdateDTO.getDateOfBirth());
        }
        if (traineeUpdateDTO.getAddress() != null) {
            trainee.setAddress(traineeUpdateDTO.getAddress());
        }
        userRepository.save(user);
        traineeRepository.save(trainee);
        
        log.info("Trainee updated: {} {}", user.getFirstName(), user.getLastName());
        return newUsername;
    }

    @Transactional
    public void updateTraineeTrainers(String traineeUsername, List<String> trainerUsernames) {
        Pair<User, Trainee> userAndTrainee = getUserTraineeByUsername(traineeUsername);
        Trainee trainee = userAndTrainee.getRight();

        List<Trainer> newTrainers = userRepository.findAllByUsernameIn(trainerUsernames).stream()
                .map(User::getTrainer)
                .filter(Objects::nonNull)
                .toList();

        trainee.getTrainers().stream()  
            .filter(trainer -> !newTrainers.contains(trainer))
            .forEach(trainer -> trainer.getTrainees().remove(trainee));

        newTrainers.forEach(trainer -> trainer.getTrainees().add(trainee));

        trainee.setTrainers(new HashSet<>(newTrainers));
        traineeRepository.save(trainee);
    }

    public void deleteTrainee(String username) throws NoSuchElementException {
        User user =  userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User with username " + username + " not found"));

        if (user.getTrainee() == null && user.getTrainer() != null) {
            throw new NoSuchElementException(THIS_USER_IS_TRAINER);
        }
        traineeRepository.delete(user.getTrainee());
        userRepository.delete(user);
    }

}
