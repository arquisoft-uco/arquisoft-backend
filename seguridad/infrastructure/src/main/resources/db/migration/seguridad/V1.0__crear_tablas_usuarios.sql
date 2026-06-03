CREATE TABLE usuarios (
    id    UUID         NOT NULL,
    email VARCHAR(100) NOT NULL,
    rol   VARCHAR(30)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_usuarios_email UNIQUE (email)
);
