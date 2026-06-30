-- =========================================================================
-- V1.3 — Crear tabla tipo_item (catálogo ADR-012) y tabla item
-- Bounded Context: fichas | HU-31: Agregar ítem a ficha de perfil
-- =========================================================================

-- ADR-012: PK semántica VARCHAR(50) — valor = TipoItem.name()
CREATE TABLE tipo_item (
    id          VARCHAR(50)  NOT NULL,
    nombre      VARCHAR(20)  NOT NULL,
    descripcion VARCHAR(500) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_tipo_item_nombre UNIQUE (nombre)
);

INSERT INTO tipo_item (id, nombre, descripcion) VALUES
    ('OBJETIVO_GENERAL',    'Objetivo General',    'Expresa de manera clara y concisa el propósito principal del proyecto. Debe describir qué se pretende lograr con la investigación o desarrollo y su impacto esperado.'),
    ('OBJETIVO_ESPECIFICO', 'Objetivo Especifico', 'Son metas concretas y detalladas que permiten alcanzar el objetivo general. Deben ser medibles, alcanzables y estar ordenados lógicamente, describiendo las acciones o pasos que se seguirán en el proyecto.'),
    ('ESTADO_DEL_ARTE',     'Estado Del Arte',     'Es una revisión de los estudios, tecnologías y desarrollos previos relacionados con el tema del proyecto. Permite identificar avances, tendencias y posibles vacíos que justifiquen la investigación.'),
    ('ANTECEDENTES',        'Antecedentes',        'Se refiere a estudios, investigaciones o proyectos previos que han abordado problemáticas similares. Ayudan a contextualizar el proyecto y a demostrar su relevancia y originalidad.'),
    ('JUSTIFICACION',       'Justificacion',       'Explica la importancia y pertinencia del proyecto. Se debe argumentar por qué es necesario llevarlo a cabo, a quién beneficiará y qué impacto puede tener en el área de estudio.'),
    ('REFERENCIAS',         'Referencias',         'Lista de fuentes bibliográficas, artículos científicos, libros, informes y otros documentos utilizados como base para la investigación. Se presentan en un formato de citación estandarizado (APA, IEEE, etc.).');

CREATE TABLE item (
    id              UUID         NOT NULL,
    tipo_item_id    VARCHAR(50)  NOT NULL,
    contenido       VARCHAR(7000) NOT NULL,
    ficha_perfil_id UUID         NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_item_tipo  FOREIGN KEY (tipo_item_id)    REFERENCES tipo_item(id),
    CONSTRAINT fk_item_ficha FOREIGN KEY (ficha_perfil_id) REFERENCES ficha_perfil(id) ON DELETE CASCADE
);
