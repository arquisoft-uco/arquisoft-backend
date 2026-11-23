-- Cuando el dueño no guarda la fecha del hecho (alta de un rol, vínculo, usuario), ocurrido_en es el corte:
-- transaction_timestamp() menos el margen, para que un evento en vivo reciente no sea pisado.
CREATE OR REPLACE FUNCTION sync.sincronizar_biblioteca(
    p_replica text DEFAULT NULL,
    p_margen_segundos integer DEFAULT 60,
    p_cerrar_ausentes boolean DEFAULT true
)
RETURNS TABLE (replica text, creadas integer, actualizadas integer, omitidas integer, cerradas integer)
LANGUAGE plpgsql
SET search_path = public, pg_temp
AS $fn$
DECLARE
    v_replicas constant text[] := ARRAY['bibliotecario', 'autor', 'proyecto_grado', 'estudiante_proyecto_grado'];
    v_corte timestamptz;
    v_candidatas integer;
    v_creadas integer;
    v_actualizadas integer;
    v_cerradas integer;
BEGIN
    PERFORM pg_advisory_xact_lock(hashtext('sync.sincronizar_biblioteca'));

    IF p_margen_segundos IS NULL OR p_margen_segundos < 0 THEN
        RAISE EXCEPTION 'p_margen_segundos debe ser mayor o igual a cero';
    END IF;
    IF p_replica IS NOT NULL AND NOT (p_replica = ANY (v_replicas)) THEN
        RAISE EXCEPTION 'Réplica desconocida "%". Válidas: %', p_replica, array_to_string(v_replicas, ', ');
    END IF;

    v_corte := transaction_timestamp() - make_interval(secs => p_margen_segundos);

    IF p_replica IS NULL OR p_replica = 'bibliotecario' THEN
        v_cerradas := 0;
        WITH origen AS (
            SELECT r.usuario_id AS id, u.identificador, u.nombre, u.email,
                   COALESCE(r.eliminado_en, v_corte) AS ocurrido_en, r.eliminado_en
              FROM sync_usuarios.bibliotecario r
              JOIN sync_usuarios.usuario u ON u.id = r.usuario_id
        ), upsert AS (
            INSERT INTO bibliotecario AS t (id, identificador, nombre, email, ocurrido_en, eliminado_en)
            SELECT id, identificador, nombre, email, ocurrido_en, eliminado_en FROM origen
            ON CONFLICT (id) DO UPDATE SET
                identificador = EXCLUDED.identificador,
                nombre = EXCLUDED.nombre,
                email = EXCLUDED.email,
                ocurrido_en = EXCLUDED.ocurrido_en,
                eliminado_en = EXCLUDED.eliminado_en
            WHERE t.ocurrido_en < EXCLUDED.ocurrido_en
              AND (t.identificador, t.nombre, t.email, t.eliminado_en IS NULL)
                  IS DISTINCT FROM (EXCLUDED.identificador, EXCLUDED.nombre, EXCLUDED.email, EXCLUDED.eliminado_en IS NULL)
            RETURNING (xmax = 0) AS creada
        )
            SELECT (SELECT count(*) FROM origen),
                   count(*) FILTER (WHERE creada),
                   count(*) FILTER (WHERE NOT creada)
              INTO v_candidatas, v_creadas, v_actualizadas
              FROM upsert;
        replica := 'bibliotecario';
        creadas := v_creadas;
        actualizadas := v_actualizadas;
        omitidas := v_candidatas - v_creadas - v_actualizadas;
        cerradas := v_cerradas;
        RAISE NOTICE 'sync.sincronizar_biblioteca réplica=% creadas=% actualizadas=% omitidas=% cerradas=%',
            replica, creadas, actualizadas, omitidas, cerradas;
        RETURN NEXT;
    END IF;

    IF p_replica IS NULL OR p_replica = 'autor' THEN
        v_cerradas := 0;
        WITH origen AS (
            SELECT r.usuario_id AS id, u.identificador, u.nombre, u.email,
                   COALESCE(r.eliminado_en, v_corte) AS ocurrido_en, r.eliminado_en
              FROM sync_usuarios.estudiante r
              JOIN sync_usuarios.usuario u ON u.id = r.usuario_id
        ), upsert AS (
            INSERT INTO autor AS t (id, identificador, nombre, email, ocurrido_en, eliminado_en)
            SELECT id, identificador, nombre, email, ocurrido_en, eliminado_en FROM origen
            ON CONFLICT (id) DO UPDATE SET
                identificador = EXCLUDED.identificador,
                nombre = EXCLUDED.nombre,
                email = EXCLUDED.email,
                ocurrido_en = EXCLUDED.ocurrido_en,
                eliminado_en = EXCLUDED.eliminado_en
            WHERE t.ocurrido_en < EXCLUDED.ocurrido_en
              AND (t.identificador, t.nombre, t.email, t.eliminado_en IS NULL)
                  IS DISTINCT FROM (EXCLUDED.identificador, EXCLUDED.nombre, EXCLUDED.email, EXCLUDED.eliminado_en IS NULL)
            RETURNING (xmax = 0) AS creada
        )
            SELECT (SELECT count(*) FROM origen),
                   count(*) FILTER (WHERE creada),
                   count(*) FILTER (WHERE NOT creada)
              INTO v_candidatas, v_creadas, v_actualizadas
              FROM upsert;
        replica := 'autor';
        creadas := v_creadas;
        actualizadas := v_actualizadas;
        omitidas := v_candidatas - v_creadas - v_actualizadas;
        cerradas := v_cerradas;
        RAISE NOTICE 'sync.sincronizar_biblioteca réplica=% creadas=% actualizadas=% omitidas=% cerradas=%',
            replica, creadas, actualizadas, omitidas, cerradas;
        RETURN NEXT;
    END IF;

    IF p_replica IS NULL OR p_replica = 'proyecto_grado' THEN
        v_cerradas := 0;
        WITH origen AS (
            SELECT pg.id, pg.titulo_proyecto AS titulo, v_corte AS ocurrido_en
              FROM sync_proyectos.proyecto_grado pg
             WHERE pg.id IN (SELECT proyecto_grado_id FROM sync_proyectos.estudiante_proyecto_grado)
        ), upsert AS (
            INSERT INTO proyecto_grado AS t (id, titulo, ocurrido_en)
            SELECT id, titulo, ocurrido_en FROM origen
            ON CONFLICT (id) DO UPDATE SET
                titulo = EXCLUDED.titulo,
                ocurrido_en = EXCLUDED.ocurrido_en
            WHERE t.ocurrido_en < EXCLUDED.ocurrido_en
              AND t.titulo IS DISTINCT FROM EXCLUDED.titulo
            RETURNING (xmax = 0) AS creada
        )
            SELECT (SELECT count(*) FROM origen),
                   count(*) FILTER (WHERE creada),
                   count(*) FILTER (WHERE NOT creada)
              INTO v_candidatas, v_creadas, v_actualizadas
              FROM upsert;
        replica := 'proyecto_grado';
        creadas := v_creadas;
        actualizadas := v_actualizadas;
        omitidas := v_candidatas - v_creadas - v_actualizadas;
        cerradas := v_cerradas;
        RAISE NOTICE 'sync.sincronizar_biblioteca réplica=% creadas=% actualizadas=% omitidas=% cerradas=%',
            replica, creadas, actualizadas, omitidas, cerradas;
        RETURN NEXT;
    END IF;

    IF p_replica IS NULL OR p_replica = 'estudiante_proyecto_grado' THEN
        WITH origen AS (
            SELECT o.estudiante_id, o.proyecto_grado_id, v_corte AS ocurrido_en
              FROM sync_proyectos.estudiante_proyecto_grado o
        ), upsert AS (
            INSERT INTO estudiante_proyecto_grado AS t (id, proyecto_grado_id, autor_id, ocurrido_en)
            SELECT gen_random_uuid(), proyecto_grado_id, estudiante_id, ocurrido_en FROM origen
            ON CONFLICT (proyecto_grado_id, autor_id) DO UPDATE SET
                eliminado_en = NULL,
                ocurrido_en = EXCLUDED.ocurrido_en
            WHERE t.eliminado_en IS NOT NULL
              AND t.ocurrido_en < EXCLUDED.ocurrido_en
            RETURNING (xmax = 0) AS creada
        ), cierre AS (
            UPDATE estudiante_proyecto_grado AS t
               SET eliminado_en = v_corte,
                   ocurrido_en = v_corte
             WHERE p_cerrar_ausentes IS TRUE
               AND t.eliminado_en IS NULL
               AND t.ocurrido_en < v_corte
               AND EXISTS (SELECT 1 FROM sync_proyectos.proyecto_grado p WHERE p.id = t.proyecto_grado_id)
               AND NOT EXISTS (SELECT 1 FROM origen o
                                WHERE o.estudiante_id = t.autor_id AND o.proyecto_grado_id = t.proyecto_grado_id)
            RETURNING 1
        )
            SELECT (SELECT count(*) FROM origen),
                   count(*) FILTER (WHERE creada),
                   count(*) FILTER (WHERE NOT creada),
                   (SELECT count(*) FROM cierre)
              INTO v_candidatas, v_creadas, v_actualizadas, v_cerradas
              FROM upsert;
        replica := 'estudiante_proyecto_grado';
        creadas := v_creadas;
        actualizadas := v_actualizadas;
        omitidas := v_candidatas - v_creadas - v_actualizadas;
        cerradas := v_cerradas;
        RAISE NOTICE 'sync.sincronizar_biblioteca réplica=% creadas=% actualizadas=% omitidas=% cerradas=%',
            replica, creadas, actualizadas, omitidas, cerradas;
        RETURN NEXT;
    END IF;

END;
$fn$;

REVOKE ALL ON FUNCTION sync.sincronizar_biblioteca(text, integer, boolean) FROM PUBLIC;
GRANT EXECUTE ON FUNCTION sync.sincronizar_biblioteca(text, integer, boolean) TO arquisoft_user;

COMMENT ON FUNCTION sync.sincronizar_biblioteca(text, integer, boolean) IS
    'Reconcilia las réplicas bibliotecario, autor, proyecto_grado, estudiante_proyecto_grado con su dueño. p_replica limita a una réplica; devuelve solo conteos.';
