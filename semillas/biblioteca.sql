-- Solo dev; no ejecutar en prod. Datos de prueba de HU-177 y HU-175 (no es migración ni recurso del módulo).
-- Idempotente: ON CONFLICT DO NOTHING. Requiere la migración de documentacion ya aplicada.
-- No toca estado_documentacion, que viene de la migración V20261112164955.

INSERT INTO bibliotecario (id, identificador, nombre, email, ocurrido_en, eliminado_en) VALUES
    ('b1b1b1b1-0000-4000-8000-000000000001', 'BIB-SEMILLA-01', 'Bibliotecario Semilla', 'bibliotecario.semilla@uco.edu.co',
     '2026-01-01T00:00:00Z', NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO entregable_proyecto_grado (id, proyecto_grado_id, version_entregable, asesor_id, estado_entregable_nombre) VALUES
    ('e1e1e1e1-0000-4000-8000-000000000001', 'a0a0a0a0-0000-4000-8000-000000000001', 1,
     'c0c0c0c0-0000-4000-8000-000000000001', 'Aprobado'),
    ('e1e1e1e1-0000-4000-8000-000000000002', 'a0a0a0a0-0000-4000-8000-000000000002', 1,
     'c0c0c0c0-0000-4000-8000-000000000001', 'Aprobado'),
    ('e1e1e1e1-0000-4000-8000-000000000003', 'a0a0a0a0-0000-4000-8000-000000000003', 2,
     'c0c0c0c0-0000-4000-8000-000000000002', 'Aprobado'),
    ('e1e1e1e1-0000-4000-8000-000000000004', 'a0a0a0a0-0000-4000-8000-000000000004', 1,
     'c0c0c0c0-0000-4000-8000-000000000002', 'Aprobado')
ON CONFLICT (id) DO NOTHING;

INSERT INTO autor (id, identificador, nombre, email, eliminado_en) VALUES
    ('d1d1d1d1-0000-4000-8000-000000000001', 'AUT-SEMILLA-01', 'Autor Semilla Uno', 'autor.uno@uco.edu.co', NULL),
    ('d1d1d1d1-0000-4000-8000-000000000002', 'AUT-SEMILLA-02', 'Autor Semilla Dos', 'autor.dos@uco.edu.co', NULL),
    ('d1d1d1d1-0000-4000-8000-000000000003', 'AUT-SEMILLA-03', 'Autor Semilla Tres', 'autor.tres@uco.edu.co', NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO documentacion (id, url, fecha_publicacion, entregable_proyecto_grado_id, bibliotecario_id, estado_documentacion_id) VALUES
    ('f1f1f1f1-0000-4000-8000-000000000001', 'https://biblioteca.uco.edu.co/documentos/semilla-pendiente.pdf',
     '2026-03-01T10:00:00Z', 'e1e1e1e1-0000-4000-8000-000000000001', 'b1b1b1b1-0000-4000-8000-000000000001', 'PENDIENTE'),
    ('f1f1f1f1-0000-4000-8000-000000000002', 'https://biblioteca.uco.edu.co/documentos/semilla-publicado-a.pdf',
     '2026-03-02T10:00:00Z', 'e1e1e1e1-0000-4000-8000-000000000002', 'b1b1b1b1-0000-4000-8000-000000000001', 'PUBLICADO'),
    ('f1f1f1f1-0000-4000-8000-000000000003', 'https://biblioteca.uco.edu.co/documentos/semilla-publicado-b.pdf',
     '2026-03-03T10:00:00Z', 'e1e1e1e1-0000-4000-8000-000000000003', 'b1b1b1b1-0000-4000-8000-000000000001', 'PUBLICADO'),
    ('f1f1f1f1-0000-4000-8000-000000000004', 'https://biblioteca.uco.edu.co/documentos/semilla-no-publicado.pdf',
     '2026-03-04T10:00:00Z', 'e1e1e1e1-0000-4000-8000-000000000004', 'b1b1b1b1-0000-4000-8000-000000000001', 'NO_PUBLICADO')
ON CONFLICT (id) DO NOTHING;

INSERT INTO autor_documentacion (id, autor_id, documentacion_id) VALUES
    ('a1a1a1a1-0000-4000-8000-000000000001', 'd1d1d1d1-0000-4000-8000-000000000001', 'f1f1f1f1-0000-4000-8000-000000000001'),
    ('a1a1a1a1-0000-4000-8000-000000000002', 'd1d1d1d1-0000-4000-8000-000000000001', 'f1f1f1f1-0000-4000-8000-000000000002'),
    ('a1a1a1a1-0000-4000-8000-000000000003', 'd1d1d1d1-0000-4000-8000-000000000002', 'f1f1f1f1-0000-4000-8000-000000000002'),
    ('a1a1a1a1-0000-4000-8000-000000000004', 'd1d1d1d1-0000-4000-8000-000000000002', 'f1f1f1f1-0000-4000-8000-000000000003'),
    ('a1a1a1a1-0000-4000-8000-000000000005', 'd1d1d1d1-0000-4000-8000-000000000003', 'f1f1f1f1-0000-4000-8000-000000000003'),
    ('a1a1a1a1-0000-4000-8000-000000000006', 'd1d1d1d1-0000-4000-8000-000000000003', 'f1f1f1f1-0000-4000-8000-000000000004')
ON CONFLICT (id) DO NOTHING;
