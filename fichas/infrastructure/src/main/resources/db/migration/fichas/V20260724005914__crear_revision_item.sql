CREATE TABLE revision_item (
    id                 UUID         PRIMARY KEY,
    item_id            UUID         NOT NULL,
    estado_revision_id VARCHAR(50)  NOT NULL,
    fecha_creacion     TIMESTAMP    NOT NULL,
    CONSTRAINT fk_rev_item        FOREIGN KEY (item_id) REFERENCES item(id) ON DELETE CASCADE,
    CONSTRAINT uk_revision_item_fecha UNIQUE (item_id, fecha_creacion)
);
