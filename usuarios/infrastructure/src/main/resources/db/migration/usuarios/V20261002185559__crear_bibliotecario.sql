-- HU-240 — Agregar información de un nuevo bibliotecario.
-- DDL tomado de mer/02_tablas_usuarios.sql. El id de la tabla de rol ES el id del usuario. NULL en eliminado_en = vigente.
CREATE TABLE bibliotecario (
    usuario_id   UUID PRIMARY KEY,
    eliminado_en TIMESTAMPTZ NULL,
    CONSTRAINT fk_bibliotecario_base FOREIGN KEY (usuario_id)
        REFERENCES usuario(id) ON DELETE CASCADE
);
