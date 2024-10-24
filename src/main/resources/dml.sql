-- Вставка даних у таблицю gym_user
INSERT INTO gym_user (first_name, last_name, username, password, is_active) VALUES
('John', 'Doe', 'John.Doe', 'password123', TRUE),
('John', 'Doe', 'John.Doe1', 'password123', TRUE),
('Jane', 'Smith', 'Jane.Smith', 'password123', TRUE),
('Jane', 'Smith', 'Jane.Smith1', 'password123', TRUE);

-- Вставка даних у таблицю training_type
INSERT INTO training_type (type_name) VALUES
('Strength'),
('Cardio');

-- Вставка даних у таблицю trainer
INSERT INTO trainer (specialization, user_id) VALUES
('Strength', 1),
('Cardio', 2);

-- Вставка даних у таблицю trainee
INSERT INTO trainee (date_of_birth, address, user_id) VALUES
('1990-05-15', '123 Main St, City', 3),
('1988-11-22', '456 Oak Ave, Town', 4);

-- Вставка даних у таблицю trainer_trainee
INSERT INTO trainer_trainee (trainer_id, trainee_id) VALUES
(1, 1),
(2, 2),
(1, 2);

-- Вставка даних у таблицю training
INSERT INTO training (trainee_id, trainer_id, training_name, training_type_id, training_date, duration) VALUES
(1, 1, 'Weight Lifting Session', 1, '2023-04-01', 60),
(2, 2, 'Treadmill Workout', 2, '2023-04-02', 45),
(2, 1, 'Strength Training', 1, '2023-04-03', 75);