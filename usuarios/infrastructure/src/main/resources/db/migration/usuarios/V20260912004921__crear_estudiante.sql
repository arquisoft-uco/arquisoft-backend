-- HU-247 — Agregar información de un nuevo estudiante.
-- DDL tomado de mer/02_tablas_usuarios.sql. El id de la tabla de rol ES el id del usuario.

CREATE TABLE estudiante (
    usuario_id UUID PRIMARY KEY,
    CONSTRAINT fk_estudiante_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuario(id) ON DELETE CASCADE
);
