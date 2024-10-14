package com.example.gymcrm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import com.example.gymcrm.dto.misc.UsernameAndPassword;
import com.example.gymcrm.dto.response.trainee.TraineeResponse;
import com.example.gymcrm.dto.response.trainer.FourFieldsTrainerResponse;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.entity.Trainer;
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

    public TraineeResponse getTraineeForResponseByUsername(String username) throws NoSuchElementException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Trainee was not found"));
        Trainee trainee = user.getTrainee();

        if (trainee == null) {
            throw new NoSuchElementException(THIS_USER_IS_TRAINER);
        }

        return getTraineeResponse(user, trainee);
    }

    @Transactional
    public UsernameAndPassword createProfile(String firstName, String lastName, LocalDate dateOfBirth, String address) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        String username = UserUtils.createUserName(firstName, lastName, userRepository);
        user.setUsername(username);
        String password = RandomStringUtils.secure().nextAlphanumeric(10);
        user.setPassword(passwordEncoder.encode(password));
        user.setActive(true);

        Trainee trainee = new Trainee();
        trainee.setAddress(address);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setTrainers(new HashSet<>());
        trainee.setTrainings(new HashSet<>());
        trainee.setUser(user);
        user.setTrainee(trainee);

        userRepository.save(user);
        traineeRepository.save(trainee);

        log.info("New trainee profile created: {} {}", user.getFirstName(), user.getLastName());
        return new UsernameAndPassword(username, password);
    }

    @Transactional
    public TraineeResponse updateTrainee(String username, String firstName, String lastName, LocalDate dateOfBirth, String address, boolean isActive) throws NoSuchElementException {
        Pair<User, Trainee> userAndTrainee = getUserTraineeByUsername(username);
        User user = userAndTrainee.getLeft();
        Trainee trainee = userAndTrainee.getRight();

        user.setFirstName(firstName);        
        user.setLastName(lastName);
        user.setActive(isActive);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);

        userRepository.save(user);
        traineeRepository.save(trainee);
        
        log.info("Trainee updated: {} {}", user.getFirstName(), user.getLastName());
        return getTraineeResponse(user, trainee);
    }

    @Transactional
    public List<FourFieldsTrainerResponse> updateTraineeTrainers(String traineeUsername, List<String> trainerUsernames) throws NoSuchElementException {
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
        return newTrainers.stream().map(this::parseTrainerToFourFieldsTraineeResponse).toList();
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

    private TraineeResponse getTraineeResponse(User user, Trainee trainee){

        TraineeResponse traineeResponse = new TraineeResponse();
        traineeResponse.setFirstName(user.getFirstName());
        traineeResponse.setLastName(user.getLastName());
        traineeResponse.setActive(user.getActive());
        traineeResponse.setDateOfBirth(trainee.getDateOfBirth());
        traineeResponse.setAddress(trainee.getAddress());

        traineeResponse.setTrainers(trainee.getTrainers().stream().map(trainer -> {
            TraineeResponse.TrainerInfo trainerInfo = traineeResponse.new TrainerInfo();
            trainerInfo.setUsername(trainer.getUser().getUsername());
            trainerInfo.setFirstName(trainer.getUser().getFirstName());
            trainerInfo.setLastName(trainer.getUser().getLastName());
            trainerInfo.setSpecialization(trainer.getSpecialization().getTypeName());
            return trainerInfo;
        }).toList());

        return traineeResponse; 
    }

    private FourFieldsTrainerResponse parseTrainerToFourFieldsTraineeResponse(Trainer trainer){
        FourFieldsTrainerResponse fourFieldsTraineeResponse = new FourFieldsTrainerResponse();
        fourFieldsTraineeResponse.setUsername(trainer.getUser().getUsername());
        fourFieldsTraineeResponse.setFirstName(trainer.getUser().getFirstName());
        fourFieldsTraineeResponse.setLastName(trainer.getUser().getLastName());
        fourFieldsTraineeResponse.setSpecialization(trainer.getSpecialization().getTypeName());
        return fourFieldsTraineeResponse;
    }

}
