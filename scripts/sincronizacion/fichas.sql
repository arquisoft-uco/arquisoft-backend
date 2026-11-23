-- Cuando el dueño no guarda la fecha del hecho (alta de un rol, vínculo, usuario), ocurrido_en es el corte:
-- transaction_timestamp() menos el margen, para que un evento en vivo reciente no sea pisado.
CREATE OR REPLACE FUNCTION sync.sincronizar_fichas(
    p_replica text DEFAULT NULL,
    p_margen_segundos integer DEFAULT 60,
    p_cerrar_ausentes boolean DEFAULT true
)
RETURNS TABLE (replica text, creadas integer, actualizadas integer, omitidas integer, cerradas integer)
LANGUAGE plpgsql
SET search_path = public, pg_temp
AS $fn$
DECLARE
    v_replicas constant text[] := ARRAY['estudiante', 'asesor_ficha', 'representante_comite_curriculum'];
    v_corte timestamptz;
    v_candidatas integer;
    v_creadas integer;
    v_actualizadas integer;
    v_cerradas integer;
BEGIN
    PERFORM pg_advisory_xact_lock(hashtext('sync.sincronizar_fichas'));

    IF p_margen_segundos IS NULL OR p_margen_segundos < 0 THEN
        RAISE EXCEPTION 'p_margen_segundos debe ser mayor o igual a cero';
    END IF;
    IF p_replica IS NOT NULL AND NOT (p_replica = ANY (v_replicas)) THEN
        RAISE EXCEPTION 'Réplica desconocida "%". Válidas: %', p_replica, array_to_string(v_replicas, ', ');
    END IF;

    v_corte := transaction_timestamp() - make_interval(secs => p_margen_segundos);

    IF p_replica IS NULL OR p_replica = 'estudiante' THEN
        v_cerradas := 0;
        WITH origen AS (
            SELECT r.usuario_id AS id, u.identificador, u.nombre, u.email,
                   COALESCE(r.eliminado_en, v_corte) AS ocurrido_en, r.eliminado_en
              FROM sync_usuarios.estudiante r
              JOIN sync_usuarios.usuario u ON u.id = r.usuario_id
        ), upsert AS (
            INSERT INTO estudiante AS t (id, identificador, nombre, email, ocurrido_en, eliminado_en)
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
        replica := 'estudiante';
        creadas := v_creadas;
        actualizadas := v_actualizadas;
        omitidas := v_candidatas - v_creadas - v_actualizadas;
        cerradas := v_cerradas;
        RAISE NOTICE 'sync.sincronizar_fichas réplica=% creadas=% actualizadas=% omitidas=% cerradas=%',
            replica, creadas, actualizadas, omitidas, cerradas;
        RETURN NEXT;
    END IF;

    IF p_replica IS NULL OR p_replica = 'asesor_ficha' THEN
        v_cerradas := 0;
        WITH origen AS (
            SELECT r.usuario_id AS id, u.identificador, u.nombre, u.email,
                   COALESCE(r.eliminado_en, v_corte) AS ocurrido_en, r.eliminado_en
              FROM sync_usuarios.asesor_ficha r
              JOIN sync_usuarios.usuario u ON u.id = r.usuario_id
        ), upsert AS (
            INSERT INTO asesor_ficha AS t (id, identificador, nombre, email, ocurrido_en, eliminado_en)
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
        replica := 'asesor_ficha';
        creadas := v_creadas;
        actualizadas := v_actualizadas;
        omitidas := v_candidatas - v_creadas - v_actualizadas;
        cerradas := v_cerradas;
        RAISE NOTICE 'sync.sincronizar_fichas réplica=% creadas=% actualizadas=% omitidas=% cerradas=%',
            replica, creadas, actualizadas, omitidas, cerradas;
        RETURN NEXT;
    END IF;

    IF p_replica IS NULL OR p_replica = 'representante_comite_curriculum' THEN
        v_cerradas := 0;
        WITH origen AS (
            SELECT r.usuario_id AS id, u.identificador, u.nombre, u.email,
                   COALESCE(r.eliminado_en, v_corte) AS ocurrido_en, r.eliminado_en
              FROM sync_usuarios.representante_comite_curriculum r
              JOIN sync_usuarios.usuario u ON u.id = r.usuario_id
        ), upsert AS (
            INSERT INTO representante_comite_curriculum AS t (id, identificador, nombre, email, ocurrido_en, eliminado_en)
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
        replica := 'representante_comite_curriculum';
        creadas := v_creadas;
        actualizadas := v_actualizadas;
        omitidas := v_candidatas - v_creadas - v_actualizadas;
        cerradas := v_cerradas;
        RAISE NOTICE 'sync.sincronizar_fichas réplica=% creadas=% actualizadas=% omitidas=% cerradas=%',
            replica, creadas, actualizadas, omitidas, cerradas;
        RETURN NEXT;
    END IF;

END;
$fn$;

REVOKE ALL ON FUNCTION sync.sincronizar_fichas(text, integer, boolean) FROM PUBLIC;
GRANT EXECUTE ON FUNCTION sync.sincronizar_fichas(text, integer, boolean) TO arquisoft_user;

COMMENT ON FUNCTION sync.sincronizar_fichas(text, integer, boolean) IS
    'Reconcilia las réplicas estudiante, asesor_ficha, representante_comite_curriculum con su dueño. p_replica limita a una réplica; devuelve solo conteos.';
