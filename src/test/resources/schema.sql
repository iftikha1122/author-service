-- schema.sql
-- Table for roles

DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS master_users;
DROP TABLE IF EXISTS AUTHORS;

CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(20) UNIQUE NOT NULL
);

-- Insert roles
INSERT INTO roles (name) VALUES ('ROLE_ADMIN') ON CONFLICT DO NOTHING;
INSERT INTO roles (name) VALUES ('ROLE_AUTHOR') ON CONFLICT DO NOTHING;

-- Table for master users
CREATE TABLE  MASTER_USERS (
    user_id BIGSERIAL PRIMARY KEY,
    user_name VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255),
    status VARCHAR(20) NOT NULL,
    role_id INT NOT NULL,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Insert master users with dynamic role resolution
INSERT INTO master_users (user_name, password, status, role_id)
VALUES ('Admin1', '$2b$12$MDTNt2DcUhG2jJcidrEHwONr7isNPddu.sPuXtBxRp8pRyAHwmyA2', 'ACTIVE',1) ON CONFLICT DO NOTHING;;


-- Table for authors
CREATE TABLE IF NOT EXISTS AUTHORS (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    status VARCHAR(20) NOT NULL,
    user_name VARCHAR(255) UNIQUE NOT NULL
);


INSERT INTO AUTHORS (first_name, last_name, status, user_name)
SELECT 'test', 'user', 'ACTIVE', 'u0007'
WHERE NOT EXISTS (SELECT 1 FROM AUTHORS WHERE user_name = 'u0007') ON CONFLICT DO NOTHING;
