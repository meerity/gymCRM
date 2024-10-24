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
import com.example.gymcrm.dto.misc.UsernameAndPassword;
import com.example.gymcrm.dto.response.trainer.FourFieldsTrainerResponse;
import com.example.gymcrm.dto.response.trainer.TrainerResponse;
import com.example.gymcrm.entity.Trainer;
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


    private Pair<User, Trainer> getUserTrainerByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User with id " + username + " not found"));

        if (user.getTrainer() == null) {
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
        if (user.getTrainer() == null) {
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
        return getTrainerResponse(user, trainer);
    }

    public List<FourFieldsTrainerResponse> getNotAssignedActiveTrainersByTraineeUsername(String traineeUsername) {
        User userTrainee = userRepository.findByUsername(traineeUsername)
                .orElseThrow(() -> new NoSuchElementException("Cannot find Trainer by this username: " + traineeUsername));        

        if (userTrainee.getTrainee() == null){
            throw new NoSuchElementException("This user is not trainee!");
        }

        List<Trainer> allTrainers = trainerRepository.findAll();
        return allTrainers.stream()
                .filter(trainer -> !trainer.getTrainees().contains(userTrainee.getTrainee()))
                .filter(trainer -> trainer.getUser().getActive())
                .map(trainer -> getNotAssignedTrainerResponse(trainer.getUser(), trainer))
                .toList();
    }

    @Transactional
    public UsernameAndPassword createProfile(String firstName, String lastName, String specialization) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        String username =  UserUtils.createUserName(firstName, lastName, userRepository);
        user.setUsername(username);
        String password = RandomStringUtils.secure().nextAlphanumeric(10);
        user.setPassword(passwordEncoder.encode(password));
        user.setActive(true);

        Trainer trainer = new Trainer();
        TrainingType trainingType = trainingTypeRepository.findByTypeName(specialization)
                .orElseThrow(() -> new NoSuchElementException("This training type is not valid!"));
        trainer.setSpecialization(trainingType);
        trainer.setTrainees(new HashSet<>());
        trainer.setTrainings(new HashSet<>());
        trainer.setUser(user);
        user.setTrainer(trainer);

        userRepository.save(user);
        trainerRepository.save(trainer);

        log.info("New trainer profile created: {} {}", user.getFirstName(), user.getLastName());
        return new UsernameAndPassword(username, password);
    }
    
    @Transactional
    public TrainerResponse updateTrainer(String username, String firstName, String lastName, String specialization, boolean isActive) {
        Pair<User, Trainer> userAndTrainer = getUserTrainerByUsername(username);
        User user = userAndTrainer.getLeft();
        Trainer trainer = userAndTrainer.getRight();

        user.setFirstName(firstName);
        user.setLastName(lastName);

        TrainingType trainingType = trainingTypeRepository.findByTypeName(specialization)
                .orElseThrow(() -> new NoSuchElementException("Training type not found"));
        trainer.setSpecialization(trainingType);   

        user.setActive(isActive);

        userRepository.save(user);
        trainerRepository.save(trainer);
        
        log.info("Trainer updated: {} {}", user.getFirstName(), user.getLastName());
        return getTrainerResponse(user, trainer);
    }

    private TrainerResponse getTrainerResponse(User user, Trainer trainer){
        TrainerResponse response = new TrainerResponse();
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setActive(user.getActive());
        response.setSpecialization(trainer.getSpecialization().getTypeName());

        response.setTrainees(
            trainer.getTrainees().stream().map(trainee -> {
                TrainerResponse.TraineeInfo traineeInfo = response.new TraineeInfo();
                traineeInfo.setUsername(trainee.getUser().getUsername());
                traineeInfo.setFirstName(trainee.getUser().getFirstName());
                traineeInfo.setLastName(trainee.getUser().getLastName());
                return traineeInfo;
            }).collect(Collectors.toList()));

        return response;
    }

    private FourFieldsTrainerResponse getNotAssignedTrainerResponse(User user, Trainer trainer) {

        FourFieldsTrainerResponse response = new FourFieldsTrainerResponse();
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setSpecialization(trainer.getSpecialization().getTypeName());
        response.setUsername(user.getUsername());

        return response;
    }


}
