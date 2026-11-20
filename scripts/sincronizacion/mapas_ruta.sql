CREATE OR REPLACE FUNCTION sync.sincronizar_mapas_ruta(
    p_replica text DEFAULT NULL,
    p_margen_segundos integer DEFAULT 60,
    p_cerrar_ausentes boolean DEFAULT true
)
RETURNS TABLE (replica text, creadas integer, actualizadas integer, omitidas integer, cerradas integer)
LANGUAGE plpgsql
SET search_path = public, pg_temp
AS $fn$
DECLARE
    v_replicas constant text[] := ARRAY['asesor', 'proyecto_grado'];
    v_corte timestamptz;
    v_candidatas integer;
    v_creadas integer;
    v_actualizadas integer;
    v_cerradas integer;
BEGIN
    PERFORM pg_advisory_xact_lock(hashtext('sync.sincronizar_mapas_ruta'));

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
            SELECT r.usuario_id AS id, u.identificador, u.nombre, u.email, r.eliminado_en
              FROM sync_usuarios.asesor r
              JOIN sync_usuarios.usuario u ON u.id = r.usuario_id
        ), upsert AS (
            INSERT INTO asesor AS t (id, identificador, nombre, email, eliminado_en)
            SELECT id, identificador, nombre, email, eliminado_en FROM origen
            ON CONFLICT (id) DO UPDATE SET
                identificador = EXCLUDED.identificador,
                nombre = EXCLUDED.nombre,
                email = EXCLUDED.email,
                eliminado_en = EXCLUDED.eliminado_en
            WHERE (t.identificador, t.nombre, t.email, t.eliminado_en IS NULL)
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
        RAISE NOTICE 'sync.sincronizar_mapas_ruta réplica=% creadas=% actualizadas=% omitidas=% cerradas=%',
            replica, creadas, actualizadas, omitidas, cerradas;
        RETURN NEXT;
    END IF;

    IF p_replica IS NULL OR p_replica = 'proyecto_grado' THEN
        v_cerradas := 0;
        WITH origen AS (
            SELECT pg.id, e.estado_proyecto_id AS estado_proyecto_grado_id, pg.coordinador_id, pg.ficha_perfil_id,
                   pg.titulo_proyecto, e.fecha_actualizacion AS ocurrido_en
              FROM sync_proyectos.proyecto_grado pg
              JOIN (SELECT DISTINCT ON (proyecto_grado_id) proyecto_grado_id, estado_proyecto_id, fecha_actualizacion
                 FROM sync_proyectos.estado_proyecto_grado
                ORDER BY proyecto_grado_id, fecha_actualizacion DESC, id DESC) e ON e.proyecto_grado_id = pg.id
        ), upsert AS (
            INSERT INTO proyecto_grado AS t (id, estado_proyecto_grado_id, coordinador_id, ficha_perfil_id, titulo_proyecto, ocurrido_en)
            SELECT id, estado_proyecto_grado_id, coordinador_id, ficha_perfil_id, titulo_proyecto, ocurrido_en FROM origen
            ON CONFLICT (id) DO UPDATE SET
                estado_proyecto_grado_id = EXCLUDED.estado_proyecto_grado_id,
                coordinador_id = EXCLUDED.coordinador_id,
                ficha_perfil_id = EXCLUDED.ficha_perfil_id,
                titulo_proyecto = EXCLUDED.titulo_proyecto,
                ocurrido_en = EXCLUDED.ocurrido_en
            WHERE (t.ocurrido_en IS NULL OR t.ocurrido_en < EXCLUDED.ocurrido_en)
              AND (t.estado_proyecto_grado_id, t.coordinador_id, t.ficha_perfil_id, t.titulo_proyecto)
                  IS DISTINCT FROM (EXCLUDED.estado_proyecto_grado_id, EXCLUDED.coordinador_id,
                                    EXCLUDED.ficha_perfil_id, EXCLUDED.titulo_proyecto)
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
        RAISE NOTICE 'sync.sincronizar_mapas_ruta réplica=% creadas=% actualizadas=% omitidas=% cerradas=%',
            replica, creadas, actualizadas, omitidas, cerradas;
        RETURN NEXT;
    END IF;

END;
$fn$;

REVOKE ALL ON FUNCTION sync.sincronizar_mapas_ruta(text, integer, boolean) FROM PUBLIC;
GRANT EXECUTE ON FUNCTION sync.sincronizar_mapas_ruta(text, integer, boolean) TO arquisoft_user;

COMMENT ON FUNCTION sync.sincronizar_mapas_ruta(text, integer, boolean) IS
    'Reconcilia las réplicas asesor, proyecto_grado con su dueño. p_replica limita a una réplica; devuelve solo conteos.';
