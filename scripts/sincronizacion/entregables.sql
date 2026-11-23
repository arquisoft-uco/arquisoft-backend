-- Cuando el dueño no guarda la fecha del hecho (alta de un rol, vínculo, usuario), ocurrido_en es el corte:
-- transaction_timestamp() menos el margen, para que un evento en vivo reciente no sea pisado.
CREATE OR REPLACE FUNCTION sync.sincronizar_entregables(
    p_replica text DEFAULT NULL,
    p_margen_segundos integer DEFAULT 60,
    p_cerrar_ausentes boolean DEFAULT true
)
RETURNS TABLE (replica text, creadas integer, actualizadas integer, omitidas integer, cerradas integer)
LANGUAGE plpgsql
SET search_path = public, pg_temp
AS $fn$
DECLARE
    v_replicas constant text[] := ARRAY['asesor', 'proyecto_grado', 'estudiante_proyecto_grado', 'asesor_proyecto_grado'];
    v_corte timestamptz;
    v_candidatas integer;
    v_creadas integer;
    v_actualizadas integer;
    v_cerradas integer;
BEGIN
    PERFORM pg_advisory_xact_lock(hashtext('sync.sincronizar_entregables'));

    IF p_margen_segundos IS NULL OR p_margen_segundos < 0 THEN
        RAISE EXCEPTION 'p_margen_segundos debe ser mayor o igual a cero';
    END IF;
    IF p_replica IS NOT NULL AND NOT (p_replica = ANY (v_replicas)) THEN
        RAISE EXCEPTION 'Réplica desconocida "%". Válidas: %', p_replica, array_to_string(v_replicas, ', ');
    END IF;

    v_corte := transaction_timestamp() - make_interval(secs => p_margen_segundos);

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
        RAISE NOTICE 'sync.sincronizar_entregables réplica=% creadas=% actualizadas=% omitidas=% cerradas=%',
            replica, creadas, actualizadas, omitidas, cerradas;
        RETURN NEXT;
    END IF;

    IF p_replica IS NULL OR p_replica = 'proyecto_grado' THEN
        v_cerradas := 0;
        WITH origen AS (
            SELECT pg.id, pg.titulo_proyecto AS nombre, e.estado_proyecto_id, e.fecha_actualizacion AS ocurrido_en
              FROM sync_proyectos.proyecto_grado pg
              JOIN (SELECT DISTINCT ON (proyecto_grado_id) proyecto_grado_id, estado_proyecto_id, fecha_actualizacion
                 FROM sync_proyectos.estado_proyecto_grado
                ORDER BY proyecto_grado_id, fecha_actualizacion DESC, id DESC) e ON e.proyecto_grado_id = pg.id
        ), upsert AS (
            INSERT INTO proyecto_grado AS t (id, nombre, estado_proyecto_id, ocurrido_en)
            SELECT id, nombre, estado_proyecto_id, ocurrido_en FROM origen
            ON CONFLICT (id) DO UPDATE SET
                nombre = EXCLUDED.nombre,
                estado_proyecto_id = EXCLUDED.estado_proyecto_id,
                ocurrido_en = EXCLUDED.ocurrido_en
            WHERE (t.ocurrido_en IS NULL OR t.ocurrido_en < EXCLUDED.ocurrido_en)
              AND (t.nombre, t.estado_proyecto_id) IS DISTINCT FROM (EXCLUDED.nombre, EXCLUDED.estado_proyecto_id)
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
        RAISE NOTICE 'sync.sincronizar_entregables réplica=% creadas=% actualizadas=% omitidas=% cerradas=%',
            replica, creadas, actualizadas, omitidas, cerradas;
        RETURN NEXT;
    END IF;

    IF p_replica IS NULL OR p_replica = 'estudiante_proyecto_grado' THEN
        WITH origen AS (
            SELECT o.estudiante_id, o.proyecto_grado_id, v_corte AS ocurrido_en
              FROM sync_proyectos.estudiante_proyecto_grado o
        ), upsert AS (
            INSERT INTO estudiante_proyecto_grado AS t (id, estudiante_id, proyecto_grado_id, ocurrido_en)
            SELECT gen_random_uuid(), estudiante_id, proyecto_grado_id, ocurrido_en FROM origen
            ON CONFLICT (estudiante_id, proyecto_grado_id) DO UPDATE SET
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
        RAISE NOTICE 'sync.sincronizar_entregables réplica=% creadas=% actualizadas=% omitidas=% cerradas=%',
            replica, creadas, actualizadas, omitidas, cerradas;
        RETURN NEXT;
    END IF;

    IF p_replica IS NULL OR p_replica = 'asesor_proyecto_grado' THEN
        v_cerradas := 0;
        WITH origen AS (
            SELECT o.asesor_id, o.proyecto_grado_id, o.fecha_asignado AS ocurrido_en,
                   CASE WHEN o.estado_asesor_id = 'FINALIZADO' THEN o.fecha_asignado END AS finalizado_en
              FROM (SELECT DISTINCT ON (asesor_id, proyecto_grado_id)
                           asesor_id, proyecto_grado_id, estado_asesor_id, fecha_asignado
                      FROM sync_proyectos.asesor_proyecto_grado
                     ORDER BY asesor_id, proyecto_grado_id, fecha_asignado DESC, id DESC) o
        ), upsert AS (
            INSERT INTO asesor_proyecto_grado AS t (id, asesor_id, proyecto_grado_id, ocurrido_en, finalizado_en)
            SELECT gen_random_uuid(), asesor_id, proyecto_grado_id, ocurrido_en, finalizado_en FROM origen
            ON CONFLICT (asesor_id, proyecto_grado_id) DO UPDATE SET
                ocurrido_en = EXCLUDED.ocurrido_en,
                finalizado_en = EXCLUDED.finalizado_en
            WHERE t.ocurrido_en < EXCLUDED.ocurrido_en
              AND (t.finalizado_en IS NULL) IS DISTINCT FROM (EXCLUDED.finalizado_en IS NULL)
            RETURNING (xmax = 0) AS creada
        )
            SELECT (SELECT count(*) FROM origen),
                   count(*) FILTER (WHERE creada),
                   count(*) FILTER (WHERE NOT creada)
              INTO v_candidatas, v_creadas, v_actualizadas
              FROM upsert;
        replica := 'asesor_proyecto_grado';
        creadas := v_creadas;
        actualizadas := v_actualizadas;
        omitidas := v_candidatas - v_creadas - v_actualizadas;
        cerradas := v_cerradas;
        RAISE NOTICE 'sync.sincronizar_entregables réplica=% creadas=% actualizadas=% omitidas=% cerradas=%',
            replica, creadas, actualizadas, omitidas, cerradas;
        RETURN NEXT;
    END IF;

END;
$fn$;

REVOKE ALL ON FUNCTION sync.sincronizar_entregables(text, integer, boolean) FROM PUBLIC;
GRANT EXECUTE ON FUNCTION sync.sincronizar_entregables(text, integer, boolean) TO arquisoft_user;

COMMENT ON FUNCTION sync.sincronizar_entregables(text, integer, boolean) IS
    'Reconcilia las réplicas asesor, proyecto_grado, estudiante_proyecto_grado, asesor_proyecto_grado con su dueño. p_replica limita a una réplica; devuelve solo conteos.';
