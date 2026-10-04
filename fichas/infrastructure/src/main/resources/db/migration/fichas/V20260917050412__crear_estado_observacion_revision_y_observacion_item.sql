CREATE TABLE estado_observacion_revision (
                                             id          VARCHAR(60)  PRIMARY KEY,
                                             nombre      VARCHAR(60)  NOT NULL,
                                             descripcion VARCHAR(300) NOT NULL,
                                             CONSTRAINT uk_estado_obs_rev_nombre UNIQUE (nombre)
);

INSERT INTO estado_observacion_revision (id, nombre, descripcion) VALUES
                                                                      ('PENDIENTE',   'Pendiente',   'La observacion revisión ha sido registrada, pero aún no se ha iniciado ninguna acción sobre ella. Está en espera de ser atendida.'),
                                                                      ('EN_PROGRESO', 'En Progreso', 'La observacion revisión está siendo trabajada activamente. Se están realizando las acciones necesarias para su resolución.'),
                                                                      ('CERRADO',     'Cerrado',     'La observacion revisión ha sido completada y no requiere más acciones. Puede significar que ha sido aprobada, resuelta o finalizada satisfactoriamente.');

CREATE TABLE observacion_item (
                                  id                             UUID         PRIMARY KEY,
                                  revision_item_id               UUID         NOT NULL,
                                  observacion                    VARCHAR(200) NOT NULL,
                                  estado_observacion_revision_id VARCHAR(60)  NOT NULL,
                                  CONSTRAINT fk_obs_item_rev    FOREIGN KEY (revision_item_id)
                                      REFERENCES revision_item(id) ON DELETE CASCADE,
                                  CONSTRAINT fk_obs_item_estado FOREIGN KEY (estado_observacion_revision_id)
                                      REFERENCES estado_observacion_revision(id),
                                  CONSTRAINT uk_obs_item_rev_msg UNIQUE (revision_item_id, observacion)
);
