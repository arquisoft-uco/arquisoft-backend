\connect usuarios

INSERT INTO usuario (id, identificador, nombre, email, contacto, estado_id) VALUES
    ('a0000000-0000-0000-0000-000000000001', 'ID-01', 'Ana Uno',       'ana@uco.test',     '3000000001', 'ACTIVO'),
    ('a0000000-0000-0000-0000-000000000002', 'ID-02', 'Beto Dos',      'beto@uco.test',    '3000000002', 'ACTIVO'),
    ('a0000000-0000-0000-0000-000000000003', 'ID-03', 'Carla Tres',    'carla@uco.test',   '3000000003', 'ACTIVO'),
    ('a0000000-0000-0000-0000-000000000004', 'ID-04', 'Dario Cuatro',  'dario@uco.test',   '3000000004', 'ACTIVO'),
    ('a0000000-0000-0000-0000-000000000005', 'ID-05', 'Elena Cinco',   'elena@uco.test',   '3000000005', 'ACTIVO'),
    ('a0000000-0000-0000-0000-000000000006', 'ID-06', 'Fabio Coord',   'fabio@uco.test',   '3000000006', 'ACTIVO'),
    ('a0000000-0000-0000-0000-000000000007', 'ID-07', 'Gloria Siete',  'gloria@uco.test',  '3000000007', 'ACTIVO'),
    ('a0000000-0000-0000-0000-000000000008', 'ID-08', 'Hugo Ocho',     'hugo@uco.test',    '3000000008', 'ACTIVO'),
    ('a0000000-0000-0000-0000-000000000009', 'ID-09', 'Irene Nueve',   'irene@uco.test',   '3000000009', 'ACTIVO'),
    ('a0000000-0000-0000-0000-000000000010', 'ID-10', 'Julia Diez',    'julia@uco.test',   '3000000010', 'ACTIVO'),
    ('a0000000-0000-0000-0000-000000000011', 'ID-11', 'Kevin Once',    'kevin@uco.test',   '3000000011', 'ACTIVO'),
    ('a0000000-0000-0000-0000-000000000012', 'ID-12', 'Laura Doce',    'laura@uco.test',   '3000000012', 'ACTIVO'),
    ('a0000000-0000-0000-0000-000000000015', 'ID-15', 'Mario Quince',  'mario@uco.test',   '3000000015', 'ACTIVO');

INSERT INTO estudiante (usuario_id, eliminado_en) VALUES
    ('a0000000-0000-0000-0000-000000000001', NULL),
    ('a0000000-0000-0000-0000-000000000002', NULL),
    ('a0000000-0000-0000-0000-000000000003', '2026-01-01T00:00:00Z');
INSERT INTO asesor (usuario_id, eliminado_en) VALUES
    ('a0000000-0000-0000-0000-000000000004', NULL),
    ('a0000000-0000-0000-0000-000000000012', '2026-01-02T00:00:00Z');
INSERT INTO asesor_ficha (usuario_id) VALUES ('a0000000-0000-0000-0000-000000000005');
INSERT INTO coordinador (usuario_id) VALUES ('a0000000-0000-0000-0000-000000000006');
INSERT INTO representante_comite_curriculum (usuario_id) VALUES ('a0000000-0000-0000-0000-000000000007');
INSERT INTO administrador (usuario_id) VALUES
    ('a0000000-0000-0000-0000-000000000008'),
    ('a0000000-0000-0000-0000-000000000015');
INSERT INTO bibliotecario (usuario_id) VALUES ('a0000000-0000-0000-0000-000000000009');
INSERT INTO jurado (usuario_id) VALUES ('a0000000-0000-0000-0000-000000000010');

\connect proyectos_grado

INSERT INTO coordinador (id, identificador, nombre, email, ocurrido_en) VALUES
    ('a0000000-0000-0000-0000-000000000006', 'ID-06', 'Fabio Coord', 'fabio@uco.test', '2026-01-01T00:00:00Z');
INSERT INTO estudiante (id, identificador, nombre, email, ocurrido_en) VALUES
    ('a0000000-0000-0000-0000-000000000001', 'ID-01', 'Ana Uno',  'ana@uco.test',  '2026-01-01T00:00:00Z'),
    ('a0000000-0000-0000-0000-000000000002', 'ID-02', 'Beto Dos', 'beto@uco.test', '2026-01-01T00:00:00Z');
INSERT INTO asesor (id, identificador, nombre, email, ocurrido_en) VALUES
    ('a0000000-0000-0000-0000-000000000004', 'ID-04', 'Dario Cuatro', 'dario@uco.test', '2026-01-01T00:00:00Z');

INSERT INTO proyecto_grado (id, coordinador_id, ficha_perfil_id, titulo_proyecto) VALUES
    ('b0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000006', 'c0000000-0000-0000-0000-000000000001', 'Proyecto Uno'),
    ('b0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000006', 'c0000000-0000-0000-0000-000000000002', 'Proyecto Dos'),
    ('b0000000-0000-0000-0000-000000000003', 'a0000000-0000-0000-0000-000000000006', 'c0000000-0000-0000-0000-000000000003', 'Proyecto Tres'),
    ('b0000000-0000-0000-0000-000000000004', 'a0000000-0000-0000-0000-000000000006', 'c0000000-0000-0000-0000-000000000004', 'Proyecto Cuatro');

INSERT INTO estado_proyecto_grado (id, proyecto_grado_id, estado_proyecto_id, fecha_actualizacion) VALUES
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000001', 'EN_PROCESO', '2026-02-01T00:00:00Z'),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000001', 'ATRASADO',   '2026-03-01T00:00:00Z'),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000002', 'EN_PROCESO', '2026-02-10T00:00:00Z'),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000003', 'EN_PROCESO', '2026-02-11T00:00:00Z');

INSERT INTO estudiante_proyecto_grado (id, estudiante_id, proyecto_grado_id) VALUES
    (gen_random_uuid(), 'a0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001'),
    (gen_random_uuid(), 'a0000000-0000-0000-0000-000000000002', 'b0000000-0000-0000-0000-000000000001'),
    (gen_random_uuid(), 'a0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000002');

INSERT INTO asesor_proyecto_grado (id, asesor_id, proyecto_grado_id, estado_asesor_id, fecha_asignado) VALUES
    (gen_random_uuid(), 'a0000000-0000-0000-0000-000000000004', 'b0000000-0000-0000-0000-000000000001', 'ASIGNADO',   '2026-02-02T00:00:00Z'),
    (gen_random_uuid(), 'a0000000-0000-0000-0000-000000000004', 'b0000000-0000-0000-0000-000000000002', 'ASIGNADO',   '2026-02-03T00:00:00Z'),
    (gen_random_uuid(), 'a0000000-0000-0000-0000-000000000004', 'b0000000-0000-0000-0000-000000000002', 'FINALIZADO', '2026-04-01T00:00:00Z');

\connect entregables

INSERT INTO asesor (id, identificador, nombre, email, ocurrido_en) VALUES
    ('a0000000-0000-0000-0000-000000000004', 'ID-04', 'Dario Cuatro', 'dario@uco.test', '2026-01-01T00:00:00Z');
INSERT INTO proyecto_grado (id, nombre, estado_proyecto_id) VALUES
    ('b0000000-0000-0000-0000-000000000001', 'Proyecto Uno',   'EN_PROCESO'),
    ('b0000000-0000-0000-0000-000000000002', 'Proyecto Dos',   'EN_PROCESO'),
    ('b0000000-0000-0000-0000-000000000005', 'Proyecto Cinco', 'EN_PROCESO');

INSERT INTO entregable_proyecto_grado (id, proyecto_grado_id, version_entregable, asesor_id) VALUES
    ('d0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001', 1, 'a0000000-0000-0000-0000-000000000004'),
    ('d0000000-0000-0000-0000-000000000002', 'b0000000-0000-0000-0000-000000000001', 2, 'a0000000-0000-0000-0000-000000000004'),
    ('d0000000-0000-0000-0000-000000000003', 'b0000000-0000-0000-0000-000000000002', 1, 'a0000000-0000-0000-0000-000000000004'),
    ('d0000000-0000-0000-0000-000000000004', 'b0000000-0000-0000-0000-000000000005', 1, 'a0000000-0000-0000-0000-000000000004');

INSERT INTO estado_entregable_proyecto_grado (id, entregable_proyecto_grado_id, estado_entregable_id, fecha_actualizacion) VALUES
    (gen_random_uuid(), 'd0000000-0000-0000-0000-000000000001', 'NUEVO',                '2026-03-01T00:00:00Z'),
    (gen_random_uuid(), 'd0000000-0000-0000-0000-000000000001', 'DISPONIBLE_EVALUACION', '2026-03-05T00:00:00Z'),
    (gen_random_uuid(), 'd0000000-0000-0000-0000-000000000002', 'NUEVO',                '2026-03-02T00:00:00Z'),
    (gen_random_uuid(), 'd0000000-0000-0000-0000-000000000003', 'NUEVO',                '2026-03-03T00:00:00Z'),
    (gen_random_uuid(), 'd0000000-0000-0000-0000-000000000003', 'EN_EVALUACION',        '2026-03-04T00:00:00Z'),
    (gen_random_uuid(), 'd0000000-0000-0000-0000-000000000004', 'DISPONIBLE_EVALUACION', '2026-03-06T00:00:00Z');

\connect repositorio_artefactos

INSERT INTO administrador (id, identificador, nombre, email, ocurrido_en) VALUES
    ('a0000000-0000-0000-0000-000000000008', 'ID-08', 'Hugo Ocho', 'hugo@uco.test', '2026-01-01T00:00:00Z');
INSERT INTO repositorio_artefacto (id, nombre_repositorio, descripcion, administrador_id) VALUES
    ('e0000000-0000-0000-0000-000000000001', 'Repo Uno', 'Repositorio de prueba', 'a0000000-0000-0000-0000-000000000008');
INSERT INTO version_repositorio_artefacto
    (id, nombre_documento, numero_version, fecha_creacion, repositorio_artefacto_id, uri_archivo, tipo_contenido, tamano_bytes, nombre_archivo) VALUES
    ('f0000000-0000-0000-0000-000000000001', 'doc1', 1, '2026-02-01T00:00:00Z', 'e0000000-0000-0000-0000-000000000001', 'bucket/doc1', 'application/pdf', 10, 'a.pdf'),
    ('f0000000-0000-0000-0000-000000000002', 'doc2', 1, '2026-02-02T00:00:00Z', 'e0000000-0000-0000-0000-000000000001', 'bucket/doc2', 'application/pdf', 20, 'b.pdf');
