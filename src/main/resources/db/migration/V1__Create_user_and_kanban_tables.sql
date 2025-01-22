-- Создание таблицы users
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR(255) UNIQUE NOT NULL,
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

-- Создание таблицы boards
CREATE TABLE boards (
                        id SERIAL PRIMARY KEY,
                        title VARCHAR(255),
                        board_owner_id INTEGER NOT NULL ,
                        FOREIGN KEY (board_owner_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Создание таблицы columns
CREATE TABLE columns (
                         id SERIAL PRIMARY KEY,
                         title VARCHAR(255),
                         board_id INTEGER NOT NULL,
                         creator_id INTEGER,
                         FOREIGN KEY (board_id) REFERENCES boards (id) ON DELETE CASCADE,
                         FOREIGN KEY (creator_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Создание таблицы cards
CREATE TABLE cards (
                       id SERIAL PRIMARY KEY,
                       title VARCHAR(255),
                       content TEXT,
                       column_id INTEGER NOT NULL,
                       user_owner_id INTEGER,
                       FOREIGN KEY (column_id) REFERENCES columns (id) ON DELETE CASCADE,
                       FOREIGN KEY (user_owner_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Создание таблицы users_accessible_boards (для связи многие-ко-многим между users и boards)
CREATE TABLE users_accessible_boards (
                                         user_id INTEGER NOT NULL,
                                         boards_id INTEGER NOT NULL,
                                         PRIMARY KEY (user_id, boards_id),
                                         FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
                                         FOREIGN KEY (boards_id) REFERENCES boards (id) ON DELETE CASCADE
);

-- Создание таблицы cards_responsible_user (для связи многие-ко-многим между cards и users)
CREATE TABLE cards_responsible_user (
                                        card_id INTEGER NOT NULL,
                                        user_id INTEGER NOT NULL,
                                        PRIMARY KEY (card_id, user_id),
                                        FOREIGN KEY (card_id) REFERENCES cards (id) ON DELETE CASCADE,
                                        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
