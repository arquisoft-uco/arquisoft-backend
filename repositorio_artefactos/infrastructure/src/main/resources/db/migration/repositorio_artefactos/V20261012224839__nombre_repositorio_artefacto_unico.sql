ALTER TABLE repositorio_artefacto DROP CONSTRAINT uk_repo_nombre_desc_admin;

ALTER TABLE repositorio_artefacto ADD CONSTRAINT uk_repo_nombre UNIQUE (nombre_repositorio);
