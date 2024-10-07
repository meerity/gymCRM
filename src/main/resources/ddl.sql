CREATE TABLE IF NOT EXISTS gym_user (  
    id SERIAL PRIMARY KEY,
    first_name VARCHAR NOT NULL,
    last_name VARCHAR NOT NULL,
    username VARCHAR UNIQUE NOT NULL,
    password VARCHAR NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS training_type(
    id SERIAL PRIMARY KEY,
    type_name VARCHAR NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS trainer (
    id SERIAL PRIMARY KEY,
    specialization VARCHAR NOT NULL,
    user_id INTEGER NOT NULL,
    FOREIGN KEY (user_id) REFERENCES gym_user (id),
    FOREIGN KEY (specialization) REFERENCES training_type (type_name)
);

CREATE TABLE IF NOT EXISTS trainee (
    id SERIAL PRIMARY KEY,
    date_of_birth DATE,
    address VARCHAR,
    user_id INTEGER NOT NULL,
    FOREIGN KEY (user_id) REFERENCES gym_user (id)
);

CREATE TABLE IF NOT EXISTS trainer_trainee (
    trainer_id INT,
    trainee_id INT,
    PRIMARY KEY (trainer_id, trainee_id),
    FOREIGN KEY (trainer_id) REFERENCES trainer(id),
    FOREIGN KEY (trainee_id) REFERENCES trainee(id)
);

CREATE TABLE IF NOT EXISTS training (
    id SERIAL PRIMARY KEY,
    trainee_id INTEGER NOT NULL,
    trainer_id INTEGER NOT NULL,
    training_name VARCHAR NOT NULL,
    training_type_id INTEGER NOT NULL,
    training_date DATE NOT NULL,
    duration INTEGER NOT NULL,
    FOREIGN KEY (trainee_id) REFERENCES trainee (id),
    FOREIGN KEY (trainer_id) REFERENCES trainer (id),
    FOREIGN KEY (training_type_id) REFERENCES training_type (id)
);
