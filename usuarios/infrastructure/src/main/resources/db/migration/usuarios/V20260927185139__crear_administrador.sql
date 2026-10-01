-- HU-231 — Agregar información de un nuevo administrador.
-- DDL tomado de mer/02_tablas_usuarios.sql. El id de la tabla de rol ES el id del usuario. NULL en eliminado_en = vigente.
CREATE TABLE administrador (
    usuario_id   UUID PRIMARY KEY,
    eliminado_en TIMESTAMPTZ NULL,
    CONSTRAINT fk_admin_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuario(id) ON DELETE CASCADE
);
