# --- !Ups

CREATE TABLE role(
                     id   INTEGER NOT NULL PRIMARY KEY,
                     name VARCHAR
);

CREATE TABLE user(
                     id         VARCHAR    NOT NULL PRIMARY KEY,
                     first_name VARCHAR,
                     last_name  VARCHAR,
                     email      VARCHAR,
                     role_id    INTEGER     NOT NULL,
                     avatar_url VARCHAR,
                     CONSTRAINT auth_user_role_id_fk FOREIGN KEY (role_id) REFERENCES role(id)
);

CREATE TABLE login_info(
                           id           INTEGER NOT NULL PRIMARY KEY,
                           provider_id  VARCHAR,
                           provider_key VARCHAR
);

CREATE TABLE user_login_info(
                                user_id       VARCHAR   NOT NULL,
                                login_info_id INTEGER NOT NULL,
                                CONSTRAINT auth_user_login_info_user_id_fk FOREIGN KEY (user_id) REFERENCES user(id),
                                CONSTRAINT auth_user_login_info_login_info_id_fk FOREIGN KEY (login_info_id) REFERENCES login_info(id)
);

CREATE TABLE oauth2_info (
                             id            INTEGER NOT NULL PRIMARY KEY,
                             access_token  VARCHAR   NOT NULL,
                             token_type    VARCHAR,
                             expires_in    INTEGER,
                             refresh_token VARCHAR,
                             login_info_id INTEGER    NOT NULL,
                             CONSTRAINT auth_oauth2_info_login_info_id_fk FOREIGN KEY (login_info_id) REFERENCES login_info(id)
);

INSERT INTO role (id, name) VALUES (1, 'user'), (2, 'admin');

# --- !Downs

DROP TABLE oauth2_info;
DROP TABLE user_login_info;
DROP TABLE login_info;
DROP TABLE user;
DROP TABLE role;