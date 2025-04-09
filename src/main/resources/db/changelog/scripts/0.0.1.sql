--liquibase formatted sql

--changeset DanielK:1


CREATE TABLE IF NOT EXISTS telegram_users (
                                              telegram_id BIGINT NOT NULL PRIMARY KEY,
                                              username TEXT,
                                              firstname TEXT,
                                              lastname TEXT,
                                              language_code TEXT,
                                              bot_state TEXT,
                                              actual_ai_conversation_id BIGINT,
                                              updated TIMESTAMP,
                                              created TIMESTAMP
);

CREATE TABLE IF NOT EXISTS users (
                                     id serial PRIMARY KEY,
                                     name TEXT,
                                     balance NUMERIC,
                                     telegram_id BIGINT,
                                     job_tittle TEXT,
                                     properties TEXT,
                                     updated TIMESTAMP,
                                     created TIMESTAMP,
                                     FOREIGN KEY (telegram_id) REFERENCES telegram_users(telegram_id) ON DELETE CASCADE
);
