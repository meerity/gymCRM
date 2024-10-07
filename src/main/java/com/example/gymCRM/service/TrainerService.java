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
import java.util.stream.Collectors;
import com.example.gymcrm.dto.TrainerDTO;
import com.example.gymcrm.dto.response.TrainerResponse;
import com.example.gymcrm.dto.update.TrainerUpdateDTO;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.entity.Training;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.entity.TrainingType;
import com.example.gymcrm.repository.TrainerRepository;
import com.example.gymcrm.repository.TrainingTypeRepository;
import com.example.gymcrm.repository.UserRepository;
import com.example.gymcrm.util.UserUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerService {

    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String THIS_USER_IS_TRAINEE = "This user is trainee!";


    private Pair<User, Trainer> getUserTrainerByUsername(String username) throws NoSuchElementException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User with id " + username + " not found"));

        if (user.getTrainer() == null && user.getTrainee() != null) {
            throw new NoSuchElementException(THIS_USER_IS_TRAINEE);
        }

        Trainer trainer = user.getTrainer();
        return Pair.of(user, trainer);
    }

    public List<Trainer> getAllTrainers() {
        return trainerRepository.findAll();
    }

    public Trainer getTrainerById(int id) {
        return trainerRepository.findById(id).orElseThrow();
    }

    public Trainer getTrainerByUsername(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Trainer was not found"));
        if (user.getTrainer() == null && user.getTrainer() != null) {
            throw new NoSuchElementException(THIS_USER_IS_TRAINEE);
        } else {
            return user.getTrainer();
        }
    }

    public TrainerResponse getTrainerForResponseByUsername(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Trainer was not found"));
        Trainer trainer = user.getTrainer();

        if (trainer == null) {
            throw new NoSuchElementException(THIS_USER_IS_TRAINEE);
        }
        return parseTrainertoResponse(trainer);
    }

    public List<TrainerResponse> getNotAssignedTrainersByTraineeUsername(String traineeUsername) throws NoSuchElementException {
        User userTrainee = userRepository.findByUsername(traineeUsername)
                .orElseThrow(() -> new NoSuchElementException("Cannot find Trainer by this username: " + traineeUsername));        

        if (userTrainee.getTrainee() == null){
            throw new NoSuchElementException("This user is not trainee!");
        }

        List<Trainer> allTrainers = trainerRepository.findAll();
        return allTrainers.stream()
                .filter(trainer -> !trainer.getTrainees().contains(userTrainee.getTrainee()))
                .map(this::parseTrainertoResponse)
                .toList();
    }

    @Transactional
    public Pair<String, String> createProfile(TrainerDTO trainerDTO) {
        User user = new User();
        user.setFirstName(trainerDTO.getFirstName());
        user.setLastName(trainerDTO.getLastName());
        String username =  UserUtils.createUserName(trainerDTO.getFirstName(), trainerDTO.getLastName(), userRepository);
        user.setUsername(username);
        String password = RandomStringUtils.secure().nextAlphanumeric(10);
        user.setPassword(passwordEncoder.encode(password));
        user.setActive(trainerDTO.getIsActive());

        Trainer trainer = new Trainer();
        TrainingType trainingType = trainingTypeRepository.findByTypeName(trainerDTO.getSpecialization())
                .orElseThrow(() -> new NoSuchElementException("This training type is not valid!"));
        trainer.setSpecialization(trainingType);
        trainer.setTrainees(new HashSet<>());
        trainer.setTrainings(new HashSet<>());
        trainer.setUser(user);
        user.setTrainer(trainer);

        userRepository.save(user);
        trainerRepository.save(trainer);

        log.info("New trainer profile created: {} {}", user.getFirstName(), user.getLastName());
        return Pair.of(username, password);
    }
    

    public String updateTrainer(String username, TrainerUpdateDTO trainerUpdateDTO) throws NoSuchElementException {
        Pair<User, Trainer> userAndTrainer = getUserTrainerByUsername(username);
        User user = userAndTrainer.getLeft();
        Trainer trainer = userAndTrainer.getRight();

        boolean newFirstnameNotNull = trainerUpdateDTO.getFirstName() != null;
        boolean newLastnameNotNull = trainerUpdateDTO.getLastName() != null;
        String newUsername;
        
        if (newFirstnameNotNull || newLastnameNotNull) {
            if (newFirstnameNotNull) {
                user.setFirstName(trainerUpdateDTO.getFirstName());
            }
            if (newLastnameNotNull) {
                user.setLastName(trainerUpdateDTO.getLastName());
            }
            newUsername = UserUtils.createUserName(user.getFirstName(), user.getLastName(), userRepository);
            user.setUsername(newUsername);
        } else {
            newUsername = user.getUsername();
        }

        if (trainerUpdateDTO.getSpecialization() != null) {
            TrainingType trainingType = trainingTypeRepository.findByTypeName(trainerUpdateDTO.getSpecialization())
                .orElseThrow(() -> new NoSuchElementException("Training type not found"));
            trainer.setSpecialization(trainingType);    
        }

        userRepository.save(user);
        trainerRepository.save(trainer);
        
        log.info("Trainer updated: {} {}", user.getFirstName(), user.getLastName());
        return newUsername;
    }

    private TrainerResponse parseTrainertoResponse(Trainer trainer){
        TrainerResponse response = new TrainerResponse();
        response.setFirstName(trainer.getUser().getFirstName());
        response.setLastName(trainer.getUser().getLastName());
        response.setUsername(trainer.getUser().getUsername());
        response.setActive(trainer.getUser().getActive());
        response.setSpecialization(trainer.getSpecialization().getTypeName());
        response.setTraineeUsernames(trainer.getTrainees().stream().map(trainee -> trainee.getUser().getUsername()).collect(Collectors.toSet()));
        response.setTrainingNames(trainer.getTrainings().stream().map(Training::getTrainingName).collect(Collectors.toSet()));
        return response;
    }


}
