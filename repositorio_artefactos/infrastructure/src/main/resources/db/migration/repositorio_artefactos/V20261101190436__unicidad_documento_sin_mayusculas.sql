DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM version_repositorio_artefacto
        GROUP BY repositorio_artefacto_id, lower(nombre_documento)
        HAVING count(DISTINCT nombre_documento) > 1
    ) THEN
        RAISE EXCEPTION 'Existen documentos de un mismo repositorio cuyo nombre solo difiere en mayusculas; unificar los nombres antes de migrar';
    END IF;
END $$;

CREATE UNIQUE INDEX uk_vra_documento_ci_version_repo
    ON version_repositorio_artefacto (repositorio_artefacto_id, lower(nombre_documento), numero_version);
