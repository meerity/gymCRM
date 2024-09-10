package com.example.gymCRM;

import com.example.gymCRM.entity.Trainee;
import com.example.gymCRM.entity.Trainer;
import com.example.gymCRM.entity.Training;
import com.example.gymCRM.entity.TrainingType;
import com.example.gymCRM.service.TraineeService;
import com.example.gymCRM.service.TrainerService;
import com.example.gymCRM.service.TrainingService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

@Component
public class ConsoleApp implements CommandLineRunner {


    private final TrainerService trainerService;
    private final TraineeService traineeService;
    private final TrainingService trainingService;

    public ConsoleApp(TrainerService trainerService, TraineeService traineeService, TrainingService trainingService) {
        this.trainerService = trainerService;
        this.traineeService = traineeService;
        this.trainingService = trainingService;
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("CRM Console Application");
            System.out.println("1. Trainers");
            System.out.println("2. Trainees");
            System.out.println("3. Trainings");
            System.out.println("4. Exit");

            int mainChoice = scanner.nextInt();
            scanner.nextLine();

            if (mainChoice == 4) {
                System.out.println("Exiting...");
                return;
            }

            switch (mainChoice) {
                case 1:
                    handleTrainers(scanner);
                    break;
                case 2:
                    handleTrainees(scanner);
                    break;
                case 3:
                    handleTrainings(scanner);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void handleTrainers(Scanner scanner) {
        while (true) {
            System.out.println("Trainer Menu:");
            System.out.println("1. List Trainers");
            System.out.println("2. Find By Id");
            System.out.println("3. Add Trainer");
            System.out.println("4. Update Trainer");
            System.out.println("5. Back");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("List of Trainers:");
                    trainerService.getAllTrainers().forEach(System.out::println);
                    break;
                case 2:
                    System.out.println("Enter Trainer ID:");
                    Trainer trainer = trainerService.getTrainerById(scanner.nextInt());
                    if (trainer == null) {
                        System.out.println("Trainer not found");
                    } else {
                        System.out.println("Found: " + trainer);
                    }
                    break;
                case 3:
                    System.out.println("Enter first name:");
                    String firstName = scanner.nextLine();
                    System.out.println("Enter last name:");
                    String lastName = scanner.nextLine();
                    System.out.println("Enter specialization:");
                    String specialization = scanner.nextLine();
                    System.out.println("Make active? (1 - Yes, 2 - No)");
                    boolean isActive = scanner.nextInt() == 1;
                    trainerService.addTrainer(firstName, lastName, specialization, isActive);
                    break;
                case 4:
                    System.out.println("Enter trainer ID:");
                    int trainerID = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Enter new first name:");
                    String newFirstName = scanner.nextLine();
                    System.out.println("Enter new last name:");
                    String newLastName = scanner.nextLine();
                    System.out.println("Enter new specialization:");
                    String newSpecialization = scanner.nextLine();
                    System.out.println("Make active? (1 - Yes, 2 - No)");
                    boolean newIsActive = scanner.nextInt() == 1;
                    boolean success = trainerService.updateTrainer(trainerID, newFirstName, newLastName, newSpecialization, newIsActive);
                    if (success) {
                        System.out.println("Trainer updated successfully");
                    } else {
                        System.out.println("Trainer update failed");
                    }
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void handleTrainees(Scanner scanner) {
        while (true) {
            System.out.println("Trainee Menu:");
            System.out.println("1. List Trainees");
            System.out.println("2. Find By Id");
            System.out.println("3. Add Trainee");
            System.out.println("4. Update Trainee");
            System.out.println("5. Delete Trainee");
            System.out.println("6. Back");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("List of Trainees:");
                    traineeService.getAllTrainees().forEach(System.out::println);
                    break;
                case 2:
                    System.out.println("Enter Trainee ID:");
                    Trainee trainee = traineeService.getTraineeById(scanner.nextInt());
                    if (trainee == null) {
                        System.out.println("Trainee not found");
                    } else {
                        System.out.println("Found: " + trainee);
                    }
                    break;
                case 3:
                    System.out.println("Enter first name:");
                    String firstName = scanner.nextLine();
                    System.out.println("Enter last name:");
                    String lastName = scanner.nextLine();
                    System.out.println("Enter date of birth (YYYY-MM-DD):");
                    LocalDate dateOfBirth = LocalDate.parse(scanner.nextLine());
                    System.out.println("Enter address:");
                    String address = scanner.nextLine();
                    System.out.println("Make active? (1 - Yes, 2 - No)");
                    boolean isActive = scanner.nextInt() == 1;
                    traineeService.addTrainee(firstName, lastName, dateOfBirth, address, isActive);
                    break;
                case 4:
                    System.out.println("Enter trainee ID:");
                    int traineeID = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Enter new first name:");
                    String newFirstName = scanner.nextLine();
                    System.out.println("Enter new last name:");
                    String newLastName = scanner.nextLine();
                    System.out.println("Enter new date of birth (YYYY-MM-DD):");
                    LocalDate newDateOfBirth = LocalDate.parse(scanner.nextLine());
                    System.out.println("Enter new address:");
                    String newAddress = scanner.nextLine();
                    System.out.println("Make active? (1 - Yes, 2 - No)");
                    boolean newIsActive = scanner.nextInt() == 1;
                    boolean success = traineeService.updateTrainee(traineeID, newFirstName, newLastName, newDateOfBirth, newAddress, newIsActive);
                    if (success) {
                        System.out.println("Trainee updated successfully");
                    } else {
                        System.out.println("Trainee update failed");
                    }
                    break;
                case 5:
                    System.out.println("Enter trainee ID:");
                    int traineeIDToDelete = scanner.nextInt();
                    boolean successDeletion = traineeService.deleteTrainee(traineeIDToDelete);
                    if (successDeletion) {
                        System.out.println("Trainee deleted successfully");
                    } else {
                        System.out.println("Trainee delete failed");
                    }
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void handleTrainings(Scanner scanner) {
        while (true) {
            System.out.println("Training Management");
            System.out.println("1. List All Trainings");
            System.out.println("2. Find Training by Name");
            System.out.println("3. Add New Training");
            System.out.println("4. Back to Main Menu");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Считать оставшийся символ новой строки после nextInt()

            switch (choice) {
                case 1:
                    List<Training> trainings = trainingService.getAllTrainings();
                    if (trainings.isEmpty()) {
                        System.out.println("No trainings found.");
                    } else {
                        trainings.forEach(System.out::println);
                    }
                    break;
                case 2:
                    System.out.println("Enter training name:");
                    String trainingName = scanner.nextLine();
                    Training foundTraining = trainingService.getTrainingByName(trainingName);
                    if (foundTraining == null) {
                        System.out.println("No training found with the name: " + trainingName);
                    } else {
                        System.out.println(foundTraining);
                    }
                    break;
                case 3:
                    System.out.println("Enter trainee ID:");
                    int traineeId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Enter trainer ID:");
                    int trainerId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Enter training name:");
                    String newTrainingName = scanner.nextLine();
                    System.out.println("Enter training type (e.g., CARDIO, STRENGTH):");
                    String trainingTypeString = scanner.nextLine();
                    System.out.println("Enter training date (YYYY-MM-DD):");
                    LocalDate trainingDate = LocalDate.parse(scanner.nextLine());
                    System.out.println("Enter training duration (in minutes):");
                    int trainingDuration = scanner.nextInt();

                    trainingService.addTraining(traineeId, trainerId, newTrainingName, new TrainingType(trainingTypeString), trainingDate, trainingDuration);
                    System.out.println("Training added successfully!");
                    break;
                case 4:
                    return; // Вернуться в главное меню
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }


}
