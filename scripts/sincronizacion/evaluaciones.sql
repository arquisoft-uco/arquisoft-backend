-- Cuando el dueño no guarda la fecha del hecho (alta de un rol, vínculo, usuario), ocurrido_en es el corte:
-- transaction_timestamp() menos el margen, para que un evento en vivo reciente no sea pisado.
CREATE OR REPLACE FUNCTION sync.sincronizar_evaluaciones(
    p_replica text DEFAULT NULL,
    p_margen_segundos integer DEFAULT 60,
    p_cerrar_ausentes boolean DEFAULT true
)
RETURNS TABLE (replica text, creadas integer, actualizadas integer, omitidas integer, cerradas integer)
LANGUAGE plpgsql
SET search_path = public, pg_temp
AS $fn$
DECLARE
    v_replicas constant text[] := ARRAY['jurado', 'asesor', 'estudiante_proyecto_grado', 'coordinador_proyecto_grado', 'entregable'];
    v_corte timestamptz;
    v_candidatas integer;
    v_creadas integer;
    v_actualizadas integer;
    v_cerradas integer;
BEGIN
    PERFORM pg_advisory_xact_lock(hashtext('sync.sincronizar_evaluaciones'));

    IF p_margen_segundos IS NULL OR p_margen_segundos < 0 THEN
        RAISE EXCEPTION 'p_margen_segundos debe ser mayor o igual a cero';
    END IF;
    IF p_replica IS NOT NULL AND NOT (p_replica = ANY (v_replicas)) THEN
        RAISE EXCEPTION 'Réplica desconocida "%". Válidas: %', p_replica, array_to_string(v_replicas, ', ');
    END IF;

    v_corte := transaction_timestamp() - make_interval(secs => p_margen_segundos);

    IF p_replica IS NULL OR p_replica = 'jurado' THEN
        v_cerradas := 0;
        WITH origen AS (
            SELECT r.usuario_id AS id, u.identificador, u.nombre, u.email,
                   COALESCE(r.eliminado_en, v_corte) AS ocurrido_en, r.eliminado_en
              FROM sync_usuarios.jurado r
              JOIN sync_usuarios.usuario u ON u.id = r.usuario_id
        ), upsert AS (
            INSERT INTO jurado AS t (id, identificador, nombre, email, ocurrido_en, eliminado_en)
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
        replica := 'jurado';
        creadas := v_creadas;
        actualizadas := v_actualizadas;
        omitidas := v_candidatas - v_creadas - v_actualizadas;
        cerradas := v_cerradas;
        RAISE NOTICE 'sync.sincronizar_evaluaciones réplica=% creadas=% actualizadas=% omitidas=% cerradas=%',
            replica, creadas, actualizadas, omitidas, cerradas;
        RETURN NEXT;
    END IF;

    IF p_replica IS NULL OR p_replica = 'asesor' THEN
        v_cerradas := 0;
        WITH origen AS (
            SELECT r.usuario_id AS id, u.identificador, u.nombre, u.email,
                   COALESCE(r.eliminado_en, v_corte) AS ocurrido_en, r.eliminado_en
              FROM sync_usuarios.asesor r
              JOIN sync_usuarios.usuario u ON u.id = r.usuario_id
        ), upsert AS (
            INSERT INTO asesor AS t (id, identificador, nombre, email, ocurrido_en, eliminado_en)
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
        replica := 'asesor';
        creadas := v_creadas;
        actualizadas := v_actualizadas;
        omitidas := v_candidatas - v_creadas - v_actualizadas;
        cerradas := v_cerradas;
        RAISE NOTICE 'sync.sincronizar_evaluaciones réplica=% creadas=% actualizadas=% omitidas=% cerradas=%',
            replica, creadas, actualizadas, omitidas, cerradas;
        RETURN NEXT;
    END IF;

    IF p_replica IS NULL OR p_replica = 'estudiante_proyecto_grado' THEN
        WITH origen AS (
            SELECT o.estudiante_id, o.proyecto_grado_id, v_corte AS ocurrido_en,
                   COALESCE(e.nombre, '') AS nombre, COALESCE(e.email, '') AS email
              FROM sync_proyectos.estudiante_proyecto_grado o
              LEFT JOIN sync_proyectos.estudiante e ON e.id = o.estudiante_id
        ), upsert AS (
            INSERT INTO estudiante_proyecto_grado AS t (id, estudiante_id, proyecto_grado_id, ocurrido_en, nombre, email)
            SELECT gen_random_uuid(), estudiante_id, proyecto_grado_id, ocurrido_en, nombre, email FROM origen
            ON CONFLICT (estudiante_id, proyecto_grado_id) DO UPDATE SET
                eliminado_en = NULL,
                ocurrido_en = EXCLUDED.ocurrido_en,
                nombre = COALESCE(NULLIF(EXCLUDED.nombre, ''), t.nombre),
                email = COALESCE(NULLIF(EXCLUDED.email, ''), t.email)
            WHERE t.ocurrido_en < EXCLUDED.ocurrido_en
              AND (t.eliminado_en IS NOT NULL
                   OR (t.nombre, t.email) IS DISTINCT FROM (COALESCE(NULLIF(EXCLUDED.nombre, ''), t.nombre),
                                                            COALESCE(NULLIF(EXCLUDED.email, ''), t.email)))
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
                                WHERE o.estudiante_id = t.estudiante_id AND o.proyecto_grado_id = t.proyecto_grado_id)
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
        RAISE NOTICE 'sync.sincronizar_evaluaciones réplica=% creadas=% actualizadas=% omitidas=% cerradas=%',
            replica, creadas, actualizadas, omitidas, cerradas;
        RETURN NEXT;
    END IF;

    IF p_replica IS NULL OR p_replica = 'coordinador_proyecto_grado' THEN
        v_cerradas := 0;
        WITH origen AS (
            SELECT pg.id AS proyecto_grado_id, c.id AS coordinador_id, c.nombre, c.email, v_corte AS ocurrido_en
              FROM sync_proyectos.proyecto_grado pg
              JOIN sync_proyectos.coordinador c ON c.id = pg.coordinador_id
        ), upsert AS (
            INSERT INTO coordinador_proyecto_grado AS t (proyecto_grado_id, coordinador_id, nombre, email, ocurrido_en)
            SELECT proyecto_grado_id, coordinador_id, nombre, email, ocurrido_en FROM origen
            ON CONFLICT (proyecto_grado_id) DO UPDATE SET
                coordinador_id = EXCLUDED.coordinador_id,
                nombre = EXCLUDED.nombre,
                email = EXCLUDED.email,
                ocurrido_en = EXCLUDED.ocurrido_en
            WHERE t.ocurrido_en < EXCLUDED.ocurrido_en
              AND (t.coordinador_id, t.nombre, t.email)
                  IS DISTINCT FROM (EXCLUDED.coordinador_id, EXCLUDED.nombre, EXCLUDED.email)
            RETURNING (xmax = 0) AS creada
        )
            SELECT (SELECT count(*) FROM origen),
                   count(*) FILTER (WHERE creada),
                   count(*) FILTER (WHERE NOT creada)
              INTO v_candidatas, v_creadas, v_actualizadas
              FROM upsert;
        replica := 'coordinador_proyecto_grado';
        creadas := v_creadas;
        actualizadas := v_actualizadas;
        omitidas := v_candidatas - v_creadas - v_actualizadas;
        cerradas := v_cerradas;
        RAISE NOTICE 'sync.sincronizar_evaluaciones réplica=% creadas=% actualizadas=% omitidas=% cerradas=%',
            replica, creadas, actualizadas, omitidas, cerradas;
        RETURN NEXT;
    END IF;

    IF p_replica IS NULL OR p_replica = 'entregable' THEN
        v_cerradas := 0;
        WITH origen AS (
            SELECT e.id, pg.nombre AS proyecto, e.version_entregable, e.proyecto_grado_id
              FROM sync_entregables.entregable_proyecto_grado e
              JOIN sync_entregables.proyecto_grado pg ON pg.id = e.proyecto_grado_id
             WHERE e.id IN (SELECT entregable_proyecto_grado_id
                              FROM sync_entregables.estado_entregable_proyecto_grado
                             WHERE estado_entregable_id = 'DISPONIBLE_EVALUACION')
        ), upsert AS (
            INSERT INTO entregable AS t (id, proyecto, version_entregable, proyecto_grado_id)
            SELECT id, proyecto, version_entregable, proyecto_grado_id FROM origen
            ON CONFLICT (id) DO UPDATE SET
                proyecto = EXCLUDED.proyecto
            WHERE t.proyecto IS DISTINCT FROM EXCLUDED.proyecto
            RETURNING (xmax = 0) AS creada
        )
            SELECT (SELECT count(*) FROM origen),
                   count(*) FILTER (WHERE creada),
                   count(*) FILTER (WHERE NOT creada)
              INTO v_candidatas, v_creadas, v_actualizadas
              FROM upsert;
        replica := 'entregable';
        creadas := v_creadas;
        actualizadas := v_actualizadas;
        omitidas := v_candidatas - v_creadas - v_actualizadas;
        cerradas := v_cerradas;
        RAISE NOTICE 'sync.sincronizar_evaluaciones réplica=% creadas=% actualizadas=% omitidas=% cerradas=%',
            replica, creadas, actualizadas, omitidas, cerradas;
        RETURN NEXT;
    END IF;

END;
$fn$;

REVOKE ALL ON FUNCTION sync.sincronizar_evaluaciones(text, integer, boolean) FROM PUBLIC;
GRANT EXECUTE ON FUNCTION sync.sincronizar_evaluaciones(text, integer, boolean) TO arquisoft_user;

COMMENT ON FUNCTION sync.sincronizar_evaluaciones(text, integer, boolean) IS
    'Reconcilia las réplicas jurado, asesor, estudiante_proyecto_grado, coordinador_proyecto_grado, entregable con su dueño. p_replica limita a una réplica; devuelve solo conteos.';
