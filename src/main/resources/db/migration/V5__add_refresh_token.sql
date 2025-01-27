
CREATE TABLE refresh_token (
                               id SERIAL PRIMARY KEY,
                               token VARCHAR(255) NOT NULL,
                               user_id INTEGER UNIQUE,
                               FOREIGN KEY (user_id) REFERENCES users (id)
);
ALTER TABLE users
    ADD COLUMN refresh_token_id BIGINT;

ALTER TABLE users
    ADD FOREIGN KEY (refresh_token_id) REFERENCES refresh_token (id);