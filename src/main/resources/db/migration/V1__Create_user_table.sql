CREATE TABLE users (
       id BIGSERIAL PRIMARY KEY,
       username VARCHAR(255) NOT NULL UNIQUE,
       password VARCHAR(255) NOT NULL,
       first_name VARCHAR(255) NOT NULL,
       surname VARCHAR(255) NOT NULL,
       middle_name VARCHAR(255) NOT NULL,
       roles VARCHAR(255) DEFAULT 'ROLE_USER',
       is_enabled BOOLEAN DEFAULT TRUE,
       is_account_non_expired BOOLEAN DEFAULT TRUE,
       is_account_non_locked BOOLEAN DEFAULT TRUE,
       is_credentials_non_expired BOOLEAN DEFAULT TRUE
);
