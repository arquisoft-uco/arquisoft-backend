-- HU-175 — ocurrido_en en la réplica autor (metadato de replicación; dueño: contexto usuarios).
-- Las filas previas (solo semillas dev: nadie poblaba autor) reciben la fecha de la migración;
-- el DEFAULT solo sirve para ese relleno y se retira, como en bibliotecario.
ALTER TABLE autor ADD COLUMN ocurrido_en TIMESTAMPTZ NOT NULL DEFAULT now();
ALTER TABLE autor ALTER COLUMN ocurrido_en DROP DEFAULT;
