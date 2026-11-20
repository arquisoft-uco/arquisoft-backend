#!/usr/bin/env bash
set -uo pipefail

DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
SINC=$(dirname "$DIR")
REPO=$(cd "$SINC/../.." && pwd)

IMAGEN=${SYNC_PRUEBA_IMAGEN:-postgres:18-alpine}
SUFIJO=$$
CT=sync-pg-prueba-$SUFIJO
RED=sync-red-prueba-$SUFIJO
ALIAS=arquisoft-postgres
LECTOR_PASSWORD=prueba-lector-$SUFIJO

declare -A DB=(
    [usuarios]=usuarios [fichas]=fichas_perfil [proyectos]=proyectos_grado [artefactos]=artefactos
    [entregables]=entregables [evaluaciones]=evaluaciones [solicitudes]=solicitudes
    [biblioteca]=biblioteca [repositorio_artefactos]=repositorio_artefactos [mapas_ruta]=mapas_ruta
)
CONTEXTOS=(fichas proyectos entregables artefactos evaluaciones solicitudes biblioteca repositorio_artefactos mapas_ruta)

U01=a0000000-0000-0000-0000-000000000001
U02=a0000000-0000-0000-0000-000000000002
U03=a0000000-0000-0000-0000-000000000003
U04=a0000000-0000-0000-0000-000000000004
U05=a0000000-0000-0000-0000-000000000005
U06=a0000000-0000-0000-0000-000000000006
U07=a0000000-0000-0000-0000-000000000007
U08=a0000000-0000-0000-0000-000000000008
U09=a0000000-0000-0000-0000-000000000009
U10=a0000000-0000-0000-0000-000000000010
U11=a0000000-0000-0000-0000-000000000011
U13=a0000000-0000-0000-0000-000000000013
U14=a0000000-0000-0000-0000-000000000014
U16=a0000000-0000-0000-0000-000000000016
P01=b0000000-0000-0000-0000-000000000001
P02=b0000000-0000-0000-0000-000000000002
P03=b0000000-0000-0000-0000-000000000003
P09=b0000000-0000-0000-0000-000000000009
E01=d0000000-0000-0000-0000-000000000001
E02=d0000000-0000-0000-0000-000000000002
E03=d0000000-0000-0000-0000-000000000003
E04=d0000000-0000-0000-0000-000000000004

total=0
fallos=0

limpiar() {
    [[ -n "${SYNC_PRUEBA_CONSERVAR:-}" ]] && { echo "Contenedor conservado: $CT (red $RED)"; return; }
    docker rm -f "$CT" >/dev/null 2>&1
    docker network rm "$RED" >/dev/null 2>&1
}
trap limpiar EXIT

pg() { docker exec -i "$CT" psql -U postgres -X -q -v ON_ERROR_STOP=1 -tA "$@"; }
app() { docker exec -i "$CT" psql -U arquisoft_user -X -q -v ON_ERROR_STOP=1 -tA -F, "$@"; }
val() { pg -d "${DB[$1]:-$1}" -c "$2"; }
ejec() { pg -d "${DB[$1]:-$1}" -c "$2" >/dev/null; }
sincronizar() {
    local ctx=$1
    shift
    app -d "${DB[$ctx]}" -c "SELECT * FROM sync.sincronizar_$ctx($*)" 2>/dev/null
}
fila() { grep "^$2," <<< "$1"; }

afirmar() {
    total=$((total + 1))
    if [[ "$2" == "$3" ]]; then
        echo "ok    $1"
    else
        echo "FALLA $1: esperado [$2] actual [$3]"
        fallos=$((fallos + 1))
    fi
}

preparar() {
    SYNC_HOST=${PREPARAR_HOST:-$ALIAS} SYNC_LECTOR_PASSWORD=$LECTOR_PASSWORD \
        SYNC_PSQL="docker exec -i $CT psql -U postgres -X -q -v ON_ERROR_STOP=1" \
        "$SINC/preparar/preparar.sh"
}

echo "== Entorno: $IMAGEN"
docker network create "$RED" >/dev/null || exit 1
docker run -d --name "$CT" --network "$RED" --network-alias "$ALIAS" -e POSTGRES_PASSWORD=pgroot \
    -v "$REPO/init-db.sql:/docker-entrypoint-initdb.d/init-db.sql:ro" "$IMAGEN" >/dev/null || exit 1

for _ in $(seq 1 60); do
    sleep 2
    docker exec "$CT" sh -c 'pg_isready -q -h "$(hostname -i)" -U postgres' 2>/dev/null \
        && pg -d mapas_ruta -c 'SELECT 1' >/dev/null 2>&1 && break
done
pg -d mapas_ruta -c 'SELECT 1' >/dev/null 2>&1 || { echo "Postgres no arrancó"; exit 1; }

for ctx in usuarios fichas proyectos artefactos entregables evaluaciones solicitudes biblioteca repositorio_artefactos mapas_ruta; do
    carpeta=$ctx
    for f in $(LC_ALL=C ls "$REPO/$carpeta/infrastructure/src/main/resources/db/migration/$carpeta/"V*.sql); do
        docker exec -i "$CT" psql -U arquisoft_user -d "${DB[$ctx]}" -X -q -v ON_ERROR_STOP=1 -f - < "$f" >/dev/null \
            || { echo "Migración fallida: $f"; exit 1; }
    done
done
pg -f - < "$DIR/datos_origen.sql" || { echo "datos_origen.sql falló"; exit 1; }

echo "== Instalación y entorno"

for host in localhost 127.0.0.1 ::1; do
    salida=$(PREPARAR_HOST=$host preparar 2>&1)
    codigo=$?
    afirmar "debeRechazarLoopback_cuandoSyncHostEs_$host" "1|si" "$codigo|$(grep -q 'loopback' <<< "$salida" && echo si || echo no)"
done

hba=$(docker exec "$CT" sh -c 'grep -v "^#" "$PGDATA/pg_hba.conf"')
reglas=$(grep -Ec '^host\s+all\s+all\s+127\.0\.0\.1/32\s+trust|^host\s+all\s+all\s+::1/128\s+trust|^host\s+all\s+all\s+all\s+scram-sha-256' <<< "$hba")
afirmar "debeReproducirReglaScram_cuandoSeLeeElPgHbaDelContenedor" "3" "$reglas"

preparar >/dev/null 2>&1
afirmar "debePrepararSinPedirContrasenas_cuandoCorreConVariablesDeEntorno" "0" "$?"
preparar >/dev/null 2>&1
afirmar "debeSerIdempotente_cuandoPrepararCorreDosVeces" "0" "$?"

ejec fichas "CREATE SCHEMA sync_loop"
ejec fichas "CREATE SERVER sync_loop FOREIGN DATA WRAPPER postgres_fdw OPTIONS (host '127.0.0.1', port '5432', dbname 'usuarios')"
ejec fichas "CREATE USER MAPPING FOR arquisoft_user SERVER sync_loop OPTIONS (user 'sync_lector', password '$LECTOR_PASSWORD')"
ejec fichas "CREATE USER MAPPING FOR CURRENT_USER SERVER sync_loop OPTIONS (user 'sync_lector', password '$LECTOR_PASSWORD')"
ejec fichas "IMPORT FOREIGN SCHEMA public LIMIT TO (usuario) FROM SERVER sync_loop INTO sync_loop"
ejec fichas "DROP USER MAPPING FOR CURRENT_USER SERVER sync_loop"
ejec fichas "GRANT USAGE ON SCHEMA sync_loop TO arquisoft_user"
ejec fichas "GRANT SELECT ON ALL TABLES IN SCHEMA sync_loop TO arquisoft_user"
error=$(app -d fichas_perfil -c "SELECT count(*) FROM sync_loop.usuario" 2>&1 >/dev/null)
afirmar "debeFallarConCredencialesRequeridas_cuandoServidorApuntaALoopback" "si" \
    "$(grep -q 'password or GSSAPI' <<< "$error" && echo si || echo no)"
ejec fichas "DROP SERVER sync_loop CASCADE"
ejec fichas "DROP SCHEMA sync_loop CASCADE"

error=$(docker exec -e PGPASSWORD="$LECTOR_PASSWORD" "$CT" psql -h "$ALIAS" -U sync_lector -d usuarios -X -q -tA \
    -c "UPDATE usuario SET nombre = 'x'" 2>&1)
afirmar "debePermitirSoloLectura_cuandoSyncLectorIntentaEscribirEnOrigen" "si" \
    "$(grep -q 'permission denied' <<< "$error" && echo si || echo no)"
lectura=$(docker exec -e PGPASSWORD="$LECTOR_PASSWORD" "$CT" psql -h "$ALIAS" -U sync_lector -d usuarios -X -q -tA \
    -c "SELECT count(*) FROM usuario" 2>&1)
afirmar "debeLeerOrigen_cuandoSyncLectorTieneSelect" "13" "$lectura"
error=$(docker exec -e PGPASSWORD="$LECTOR_PASSWORD" "$CT" psql -h "$ALIAS" -U sync_lector -d usuarios -X -q -tA \
    -c "SELECT count(*) FROM event_publication" 2>&1)
afirmar "debeNegarTablaFueraDeLaMatriz_cuandoSyncLectorLeeEventPublication" "si" \
    "$(grep -q 'permission denied' <<< "$error" && echo si || echo no)"

echo "== Contrato de la función"

args=$(pg -d fichas_perfil -c "SELECT proargnames FROM pg_proc p JOIN pg_namespace n ON n.oid = p.pronamespace WHERE n.nspname = 'sync' AND proname = 'sincronizar_fichas'")
afirmar "debeNoDevolverFilas_cuandoRetornaConteos" \
    "{p_replica,p_margen_segundos,p_cerrar_ausentes,replica,creadas,actualizadas,omitidas,cerradas}" "$args"

error=$(app -d fichas_perfil -c "SELECT * FROM sync.sincronizar_fichas('nada')" 2>&1 >/dev/null)
afirmar "debeLanzarExcepcion_cuandoReplicaDesconocida" "si" "$(grep -q 'Réplica desconocida' <<< "$error" && echo si || echo no)"

echo "== Primera sincronización (orden: proyectos antes que evaluaciones)"

declare -A R
for ctx in "${CONTEXTOS[@]}"; do
    R[$ctx]=$(sincronizar "$ctx")
done

esperado() { # ctx replica "c,a,o,x"
    afirmar "$4" "$2,$3" "$(fila "${R[$1]}" "$2")"
}

afirmar "debeDevolverUnaFilaPorReplica_cuandoNoSeIndicaReplica" "3" "$(wc -l <<< "${R[fichas]}" | tr -d ' ')"
esperado fichas estudiante "3,0,0,0" "debeCrear_cuandoLaReplicaNoTieneLaFila"
esperado fichas asesor_ficha "1,0,0,0" "debeCrearAsesorFicha_cuandoLaReplicaNoTieneLaFila"
esperado fichas representante_comite_curriculum "1,0,0,0" "debeCrearRepresentante_cuandoLaReplicaNoTieneLaFila"
afirmar "debeInsertarAnulada_cuandoElRolEstaDadoDeBajaEnElOrigen" "t" \
    "$(val fichas "SELECT eliminado_en IS NOT NULL FROM estudiante WHERE id = '$U03'")"
esperado proyectos coordinador "0,0,1,0" "debeOmitir_cuandoLaReplicaYaTieneLosMismosDatos"
esperado proyectos asesor "1,0,1,0" "debeCrearAsesorDeProyectos"
esperado proyectos estudiante "1,0,2,0" "debeCrearEstudianteDeProyectos"
esperado proyectos entregable_proyecto_grado "3,0,1,0" "debeOmitirEntregable_cuandoElProyectoLocalNoExiste"
afirmar "debeNoCrearEntregable_cuandoElProyectoLocalNoExiste" "0" \
    "$(val proyectos "SELECT count(*) FROM entregable_proyecto_grado WHERE id = '$E04'")"
esperado entregables asesor "1,0,1,0" "debeSincronizarAsesorDeEntregables"
esperado entregables proyecto_grado "1,1,1,0" "debeActualizarProyectoDeEntregables_cuandoElOrigenEsMasNuevo"
esperado entregables estudiante_proyecto_grado "3,0,0,0" "debeCrearVinculosDeEntregables"
esperado entregables asesor_proyecto_grado "2,0,0,0" "debeCrearAsesoresDelProyectoDeEntregables"
esperado artefactos asesor "2,0,0,0" "debeCrearAsesorDeArtefactos"
esperado artefactos estudiante "3,0,0,0" "debeCrearEstudianteDeArtefactos"
esperado artefactos version_repositorio_artefacto "2,0,0,0" "debeCrearVersionRepositorioArtefacto"
esperado artefactos proyecto_grado "3,0,0,0" "debeCrearProyectoDeArtefactos"
esperado artefactos estudiante_proyecto_grado "3,0,0,0" "debeCrearVinculosDeArtefactos"
esperado artefactos asesor_proyecto_grado "2,0,0,0" "debeCrearAsesoresDelProyectoDeArtefactos"
esperado evaluaciones jurado "1,0,0,0" "debeCrearJurado"
esperado evaluaciones asesor "2,0,0,0" "debeCrearAsesorDeEvaluaciones"
esperado evaluaciones estudiante_proyecto_grado "3,0,0,0" "debeCrearVinculosDeEvaluaciones"
esperado evaluaciones coordinador_proyecto_grado "4,0,0,0" "debeCrearCoordinadorProyectoGrado"
esperado evaluaciones entregable "2,0,0,0" "debeCrearEntregableDeEvaluaciones"
esperado solicitudes usuario "8,0,0,0" "debeCrearUsuarioDeSolicitudes"
esperado biblioteca bibliotecario "1,0,0,0" "debeCrearBibliotecario"
esperado biblioteca autor "3,0,0,0" "debeCrearAutor"
esperado biblioteca proyecto_grado "2,0,0,0" "debeCrearProyectoDeBiblioteca"
esperado biblioteca estudiante_proyecto_grado "3,0,0,0" "debeCrearVinculosDeBiblioteca"
esperado repositorio_artefactos administrador "1,0,1,0" "debeCrearAdministrador_yOmitirElYaExistente"
esperado mapas_ruta asesor "2,0,0,0" "debeCrearAsesorDeMapasRuta"
esperado mapas_ruta proyecto_grado "3,0,0,0" "debeCrearProyectoDeMapasRuta"

afirmar "debeIncluirUsuarioConUnRol_cuandoEsSolicitudesUsuario" "1" "$(val solicitudes "SELECT count(*) FROM usuario WHERE id = '$U01'")"
afirmar "debeExcluirUsuarioSinRol" "0" "$(val solicitudes "SELECT count(*) FROM usuario WHERE id = '$U11'")"
afirmar "debeIncluirSoloConVinculos_cuandoBibliotecaProyectoGrado" "0" "$(val biblioteca "SELECT count(*) FROM proyecto_grado WHERE id = '$P03'")"
afirmar "debeFiltrarDisponibleEvaluacion_cuandoEvaluacionesEntregable" "$E01,$E04" \
    "$(val evaluaciones "SELECT string_agg(id::text, ',' ORDER BY id) FROM entregable")"
afirmar "debeAplicarUltimoEstado_cuandoHayHistorial" "ATRASADO" "$(val artefactos "SELECT estado_proyecto_id FROM proyecto_grado WHERE id = '$P01'")"
afirmar "debeMarcarFinalizado_cuandoLaFilaMasRecienteEsFinalizado" "t,f" \
    "$(val artefactos "SELECT (SELECT finalizado_en IS NOT NULL FROM asesor_proyecto_grado WHERE proyecto_grado_id = '$P02'), (SELECT finalizado_en IS NOT NULL FROM asesor_proyecto_grado WHERE proyecto_grado_id = '$P01')" | tr '|' ',')"
afirmar "debeResolverContactoDeProyectos_cuandoEvaluacionesCoordinadorProyectoGrado" "Fabio Coord,fabio@uco.test" \
    "$(val evaluaciones "SELECT nombre || ',' || email FROM coordinador_proyecto_grado WHERE proyecto_grado_id = '$P01'")"
afirmar "debeResolverContactoDelEstudiante_cuandoEvaluacionesVinculo" "Ana Uno" \
    "$(val evaluaciones "SELECT nombre FROM estudiante_proyecto_grado WHERE estudiante_id = '$U01' AND proyecto_grado_id = '$P01'")"

echo "== Segunda llamada sin cambios"

acumulado=""
for ctx in "${CONTEXTOS[@]}"; do
    acumulado+=$(sincronizar "$ctx" | awk -F, '{print $2+$3+$5}')$'\n'
done
afirmar "debeOmitir_cuandoNadaCambioEnSegundaLlamada" "0" "$(awk '{s+=$1} END {print s+0}' <<< "$acumulado")"

solo=$(sincronizar fichas "'estudiante'")
afirmar "debeSincronizarSoloEsa_cuandoSeIndicaReplica" "estudiante,0,0,3,0" "$solo"

echo "== Rol: R, U y RN"

ejec usuarios "UPDATE usuario SET nombre = 'Ana Nueva' WHERE id = '$U01'"
resultado=$(sincronizar fichas "'estudiante'")
afirmar "debeActualizarNombre_cuandoElOrigenCambioYLaReplicaEsMasVieja" "estudiante,0,1,2,0|Ana Nueva" \
    "$resultado|$(val fichas "SELECT nombre FROM estudiante WHERE id = '$U01'")"

ejec fichas "UPDATE estudiante SET ocurrido_en = now() + interval '1 hour' WHERE id = '$U02'"
ejec usuarios "UPDATE usuario SET nombre = 'Beto Nuevo' WHERE id = '$U02'"
resultado=$(sincronizar fichas "'estudiante'")
afirmar "debeNoPisar_cuandoLaReplicaTieneOcurridoEnPosteriorAlCorte" "estudiante,0,0,3,0|Beto Dos" \
    "$resultado|$(val fichas "SELECT nombre FROM estudiante WHERE id = '$U02'")"

ejec usuarios "UPDATE estudiante SET eliminado_en = NULL WHERE usuario_id = '$U03'"
sincronizar fichas "'estudiante'" > /dev/null
afirmar "debeReactivar_cuandoElOrigenReactivoYLaReplicaEstabaAnulada" "t" \
    "$(val fichas "SELECT eliminado_en IS NULL FROM estudiante WHERE id = '$U03'")"

ejec usuarios "INSERT INTO usuario (id, identificador, nombre, email, contacto, estado_id) VALUES ('$U14', 'ID-14', 'Temporal', 'temp@uco.test', '3000000014', 'ACTIVO')"
ejec usuarios "INSERT INTO estudiante (usuario_id) VALUES ('$U14')"
sincronizar fichas "'estudiante'" > /dev/null
ejec usuarios "DELETE FROM estudiante WHERE usuario_id = '$U14'"
sincronizar fichas "'estudiante'" > /dev/null
afirmar "debeConservarFila_cuandoElOrigenNoLaTiene" "1" "$(val fichas "SELECT count(*) FROM estudiante WHERE id = '$U14'")"

ejec usuarios "UPDATE usuario SET nombre = 'Dario Nuevo' WHERE id = '$U04'"
resultado=$(sincronizar mapas_ruta "'asesor'")
afirmar "debeActualizarSinGuarda_cuandoMapasRutaAsesorCambia" "asesor,0,1,1,0|Dario Nuevo" \
    "$resultado|$(val mapas_ruta "SELECT nombre FROM asesor WHERE id = '$U04'")"

echo "== Vínculos y asesor del proyecto"

ejec proyectos "DELETE FROM estudiante_proyecto_grado WHERE estudiante_id = '$U02' AND proyecto_grado_id = '$P01'"
resultado=$(sincronizar entregables "'estudiante_proyecto_grado', 60, false")
afirmar "debeDejarIntacto_cuandoEsFalse" "estudiante_proyecto_grado,0,0,2,0|f" \
    "$resultado|$(val entregables "SELECT eliminado_en IS NOT NULL FROM estudiante_proyecto_grado WHERE estudiante_id = '$U02' AND proyecto_grado_id = '$P01'")"

ejec artefactos "INSERT INTO estudiante_proyecto_grado (id, estudiante_id, proyecto_grado_id, ocurrido_en) VALUES (gen_random_uuid(), '$U01', '$P09', '2026-01-01T00:00:00Z')"
resultado=$(sincronizar artefactos "'estudiante_proyecto_grado'")
afirmar "debeCerrarAusente_cuandoCerrarAusentesEsTrue" "estudiante_proyecto_grado,0,0,2,1|t" \
    "$resultado|$(val artefactos "SELECT destituido_en IS NOT NULL FROM estudiante_proyecto_grado WHERE estudiante_id = '$U02' AND proyecto_grado_id = '$P01'")"
afirmar "debeNoCerrar_cuandoElProyectoNoExisteEnElOrigen" "t" \
    "$(val artefactos "SELECT destituido_en IS NULL FROM estudiante_proyecto_grado WHERE proyecto_grado_id = '$P09'")"

ejec proyectos "INSERT INTO estudiante_proyecto_grado (id, estudiante_id, proyecto_grado_id) VALUES (gen_random_uuid(), '$U02', '$P01')"
resultado=$(sincronizar artefactos "'estudiante_proyecto_grado'")
afirmar "debeReactivarVinculo_cuandoElOrigenLoRecreo" "estudiante_proyecto_grado,0,1,2,0|t" \
    "$resultado|$(val artefactos "SELECT destituido_en IS NULL FROM estudiante_proyecto_grado WHERE estudiante_id = '$U02' AND proyecto_grado_id = '$P01'")"

ejec evaluaciones "UPDATE estudiante_proyecto_grado SET nombre = 'Nombre Local' WHERE estudiante_id = '$U01' AND proyecto_grado_id = '$P01'"
ejec proyectos "UPDATE estudiante SET nombre = '' WHERE id = '$U01'"
resultado=$(sincronizar evaluaciones "'estudiante_proyecto_grado'")
afirmar "debeConservarContacto_cuandoElOrigenTraeVacioEnEvaluaciones" "estudiante_proyecto_grado,0,0,3,0|Nombre Local" \
    "$resultado|$(val evaluaciones "SELECT nombre FROM estudiante_proyecto_grado WHERE estudiante_id = '$U01' AND proyecto_grado_id = '$P01'")"
ejec proyectos "UPDATE estudiante SET nombre = 'Beto Cambio' WHERE id = '$U02'"
resultado=$(sincronizar evaluaciones "'estudiante_proyecto_grado'")
afirmar "debeActualizarContacto_cuandoElContactoDeProyectosCambio" "estudiante_proyecto_grado,0,1,2,0|Beto Cambio" \
    "$resultado|$(val evaluaciones "SELECT nombre FROM estudiante_proyecto_grado WHERE estudiante_id = '$U02' AND proyecto_grado_id = '$P01'")"

ejec proyectos "INSERT INTO asesor_proyecto_grado (id, asesor_id, proyecto_grado_id, estado_asesor_id, fecha_asignado) VALUES (gen_random_uuid(), '$U04', '$P02', 'ASIGNADO', '2026-05-01T00:00:00Z')"
resultado=$(sincronizar artefactos "'asesor_proyecto_grado'")
afirmar "debeLimpiarFinalizado_cuandoReasignan" "asesor_proyecto_grado,0,1,1,0|t" \
    "$resultado|$(val artefactos "SELECT finalizado_en IS NULL FROM asesor_proyecto_grado WHERE proyecto_grado_id = '$P02'")"

echo "== Proyecto, coordinador, entregables y repositorio"

ejec artefactos "UPDATE proyecto_grado SET entregado_en = '2026-06-01T00:00:00Z' WHERE id = '$P01'"
ejec proyectos "INSERT INTO estado_proyecto_grado (id, proyecto_grado_id, estado_proyecto_id, fecha_actualizacion) VALUES (gen_random_uuid(), '$P01', 'FINALIZADO', '2026-07-01T00:00:00Z')"
resultado=$(sincronizar artefactos "'proyecto_grado'")
afirmar "debeConservarEntregadoEn_cuandoArtefactosActualizaProyecto" "proyecto_grado,0,1,2,0|FINALIZADO|2026-06-01" \
    "$resultado|$(val artefactos "SELECT estado_proyecto_id || '|' || to_char(entregado_en AT TIME ZONE 'UTC', 'YYYY-MM-DD') FROM proyecto_grado WHERE id = '$P01'")"

ejec proyectos "UPDATE coordinador SET nombre = 'Fabio Nuevo' WHERE id = '$U06'"
resultado=$(sincronizar evaluaciones "'coordinador_proyecto_grado'")
afirmar "debeResolverContactoDeProyectos_cuandoCambiaElCoordinador" "coordinador_proyecto_grado,0,4,0,0|Fabio Nuevo" \
    "$resultado|$(val evaluaciones "SELECT nombre FROM coordinador_proyecto_grado WHERE proyecto_grado_id = '$P01'")"

ejec repositorio_artefactos "UPDATE repositorio_artefacto SET nombre_repositorio = 'Repo Renombrado'"
ejec repositorio_artefactos "UPDATE version_repositorio_artefacto SET nombre_archivo = 'otro.pdf'"
resultado=$(sincronizar artefactos "'version_repositorio_artefacto'")
afirmar "debeActualizarSoloNombreRepositorio_cuandoVersionRepositorioArtefacto" \
    "version_repositorio_artefacto,0,2,0,0|Repo Renombrado|a.pdf" \
    "$resultado|$(val artefactos "SELECT nombre_repositorio || '|' || nombre_archivo FROM version_repositorio_artefacto WHERE id = 'f0000000-0000-0000-0000-000000000001'")"

echo "== Actualización de cada réplica"

cambio() { # nombre contexto replica sql_destino esperado
    local salida actualizadas
    salida=$(sincronizar "$2" "'$3'")
    actualizadas=$(fila "$salida" "$3" | cut -d, -f3)
    afirmar "$1" "si|$5" "$([[ ${actualizadas:-0} -ge 1 ]] && echo si || echo no)|$(val "$2" "$4")"
}
nombre_origen() { val usuarios "SELECT nombre FROM usuario WHERE id = '$1'"; }

ejec usuarios "INSERT INTO usuario (id, identificador, nombre, email, contacto, estado_id) VALUES ('$U16', 'ID-16', 'Nuevo Coord', 'nuevo@uco.test', '3000000016', 'ACTIVO')"
ejec usuarios "INSERT INTO coordinador (usuario_id) VALUES ('$U16')"
ejec usuarios "UPDATE usuario SET nombre = nombre || ' Mod' WHERE id IN ('$U04', '$U05', '$U06', '$U07', '$U08', '$U09', '$U10')"

resultado=$(sincronizar proyectos "'coordinador'")
afirmar "debeCrear_cuandoProyectosCoordinadorNoTieneLaFila" "coordinador,1,1,0,0" "$resultado"
afirmar "debeActualizarNombre_cuandoProyectosCoordinadorCambio" "$(nombre_origen $U06)" \
    "$(val proyectos "SELECT nombre FROM coordinador WHERE id = '$U06'")"

cambio debeActualizarNombre_cuandoFichasAsesorFichaCambio fichas asesor_ficha \
    "SELECT nombre FROM asesor_ficha WHERE id = '$U05'" "$(nombre_origen $U05)"
cambio debeActualizarNombre_cuandoFichasRepresentanteCambio fichas representante_comite_curriculum \
    "SELECT nombre FROM representante_comite_curriculum WHERE id = '$U07'" "$(nombre_origen $U07)"
cambio debeActualizarNombre_cuandoProyectosAsesorCambio proyectos asesor \
    "SELECT nombre FROM asesor WHERE id = '$U04'" "$(nombre_origen $U04)"
cambio debeActualizarNombre_cuandoProyectosEstudianteCambio proyectos estudiante \
    "SELECT nombre FROM estudiante WHERE id = '$U01'" "$(nombre_origen $U01)"
cambio debeActualizarNombre_cuandoEntregablesAsesorCambio entregables asesor \
    "SELECT nombre FROM asesor WHERE id = '$U04'" "$(nombre_origen $U04)"
cambio debeActualizarNombre_cuandoArtefactosAsesorCambio artefactos asesor \
    "SELECT nombre FROM asesor WHERE id = '$U04'" "$(nombre_origen $U04)"
cambio debeActualizarNombre_cuandoArtefactosEstudianteCambio artefactos estudiante \
    "SELECT nombre FROM estudiante WHERE id = '$U01'" "$(nombre_origen $U01)"
cambio debeActualizarNombre_cuandoEvaluacionesJuradoCambio evaluaciones jurado \
    "SELECT nombre FROM jurado WHERE id = '$U10'" "$(nombre_origen $U10)"
cambio debeActualizarNombre_cuandoEvaluacionesAsesorCambio evaluaciones asesor \
    "SELECT nombre FROM asesor WHERE id = '$U04'" "$(nombre_origen $U04)"
cambio debeActualizarNombre_cuandoSolicitudesUsuarioCambio solicitudes usuario \
    "SELECT nombre FROM usuario WHERE id = '$U08'" "$(nombre_origen $U08)"
cambio debeActualizarNombre_cuandoBibliotecaBibliotecarioCambio biblioteca bibliotecario \
    "SELECT nombre FROM bibliotecario WHERE id = '$U09'" "$(nombre_origen $U09)"
cambio debeActualizarNombre_cuandoBibliotecaAutorCambio biblioteca autor \
    "SELECT nombre FROM autor WHERE id = '$U01'" "$(nombre_origen $U01)"
cambio debeActualizarNombre_cuandoRepositorioAdministradorCambio repositorio_artefactos administrador \
    "SELECT nombre FROM administrador WHERE id = '$U08'" "$(nombre_origen $U08)"

ejec entregables "INSERT INTO estado_entregable_proyecto_grado (id, entregable_proyecto_grado_id, estado_entregable_id, fecha_actualizacion) VALUES (gen_random_uuid(), '$E01', 'EN_EVALUACION', '2026-09-01T00:00:00Z')"
cambio debeAplicarNuevoEstado_cuandoProyectosEntregableCambio proyectos entregable_proyecto_grado \
    "SELECT estado_entregable_id FROM entregable_proyecto_grado WHERE id = '$E01'" "EN_EVALUACION"

ejec entregables "UPDATE proyecto_grado SET nombre = 'Proyecto Uno Editado' WHERE id = '$P01'"
cambio debeActualizarProyecto_cuandoEvaluacionesEntregableCambio evaluaciones entregable \
    "SELECT proyecto FROM entregable WHERE id = '$E01'" "Proyecto Uno Editado"

ejec proyectos "UPDATE proyecto_grado SET titulo_proyecto = 'Proyecto Uno Retitulado' WHERE id = '$P01'"
cambio debeActualizarTitulo_cuandoBibliotecaProyectoCambio biblioteca proyecto_grado \
    "SELECT titulo FROM proyecto_grado WHERE id = '$P01'" "Proyecto Uno Retitulado"
cambio debeActualizarNombre_cuandoEntregablesProyectoCambio entregables proyecto_grado \
    "SELECT nombre FROM proyecto_grado WHERE id = '$P01'" "Proyecto Uno Retitulado"
cambio debeActualizarTitulo_cuandoMapasRutaProyectoCambio mapas_ruta proyecto_grado \
    "SELECT titulo_proyecto FROM proyecto_grado WHERE id = '$P01'" "Proyecto Uno Retitulado"

ejec proyectos "DELETE FROM estudiante_proyecto_grado WHERE estudiante_id = '$U01' AND proyecto_grado_id = '$P02'"
for ctx in entregables evaluaciones biblioteca; do
    columna=estudiante_id
    [[ $ctx == biblioteca ]] && columna=autor_id
    resultado=$(sincronizar "$ctx" "'estudiante_proyecto_grado'")
    afirmar "debeCerrarAusente_cuando${ctx}CerrarAusentesEsTrue" "1|t" \
        "$(fila "$resultado" estudiante_proyecto_grado | cut -d, -f5)|$(val "$ctx" "SELECT eliminado_en IS NOT NULL FROM estudiante_proyecto_grado WHERE $columna = '$U01' AND proyecto_grado_id = '$P02'")"
done
ejec proyectos "INSERT INTO estudiante_proyecto_grado (id, estudiante_id, proyecto_grado_id) VALUES (gen_random_uuid(), '$U01', '$P02')"
cambio debeReactivarVinculo_cuandoEntregablesLoRecreo entregables estudiante_proyecto_grado \
    "SELECT eliminado_en IS NULL FROM estudiante_proyecto_grado WHERE estudiante_id = '$U01' AND proyecto_grado_id = '$P02'" "t"
cambio debeReactivarVinculo_cuandoEvaluacionesLoRecreo evaluaciones estudiante_proyecto_grado \
    "SELECT eliminado_en IS NULL FROM estudiante_proyecto_grado WHERE estudiante_id = '$U01' AND proyecto_grado_id = '$P02'" "t"
cambio debeReactivarVinculo_cuandoBibliotecaLoRecreo biblioteca estudiante_proyecto_grado \
    "SELECT eliminado_en IS NULL FROM estudiante_proyecto_grado WHERE autor_id = '$U01' AND proyecto_grado_id = '$P02'" "t"

ejec proyectos "INSERT INTO asesor_proyecto_grado (id, asesor_id, proyecto_grado_id, estado_asesor_id, fecha_asignado) VALUES (gen_random_uuid(), '$U04', '$P01', 'FINALIZADO', '2026-09-01T00:00:00Z')"
cambio debeMarcarFinalizado_cuandoEntregablesAsesorDelProyectoCambio entregables asesor_proyecto_grado \
    "SELECT (SELECT finalizado_en IS NOT NULL FROM asesor_proyecto_grado WHERE proyecto_grado_id = '$P01')::text || (SELECT finalizado_en IS NULL FROM asesor_proyecto_grado WHERE proyecto_grado_id = '$P02')::text" "truetrue"

echo "== Idempotencia de las 30 réplicas"

REPLICAS=(
    fichas:estudiante fichas:asesor_ficha fichas:representante_comite_curriculum
    proyectos:coordinador proyectos:asesor proyectos:estudiante proyectos:entregable_proyecto_grado
    artefactos:asesor artefactos:estudiante artefactos:version_repositorio_artefacto artefactos:proyecto_grado
    artefactos:estudiante_proyecto_grado artefactos:asesor_proyecto_grado
    entregables:asesor entregables:proyecto_grado entregables:estudiante_proyecto_grado entregables:asesor_proyecto_grado
    evaluaciones:jurado evaluaciones:asesor evaluaciones:estudiante_proyecto_grado evaluaciones:coordinador_proyecto_grado
    evaluaciones:entregable
    solicitudes:usuario
    biblioteca:bibliotecario biblioteca:autor biblioteca:proyecto_grado biblioteca:estudiante_proyecto_grado
    repositorio_artefactos:administrador
    mapas_ruta:asesor mapas_ruta:proyecto_grado
)
afirmar "debeCubrirTreintaReplicas_cuandoSeListanParaIdempotencia" "30" "${#REPLICAS[@]}"
for ctx in "${CONTEXTOS[@]}"; do
    sincronizar "$ctx" > /dev/null
done
for par in "${REPLICAS[@]}"; do
    salida=$(sincronizar "${par%%:*}" "'${par##*:}'")
    afirmar "debeSerIdempotente_cuando${par%%:*}.${par##*:}SeSincronizaDosVeces" "0,0,0" \
        "$(fila "$salida" "${par##*:}" | cut -d, -f2,3,5)"
done

echo "== Transacción"

ejec usuarios "UPDATE usuario SET nombre = 'Ana Revertida' WHERE id = '$U01'"
ejec usuarios "INSERT INTO usuario (id, identificador, nombre, email, contacto, estado_id) VALUES ('$U13', 'ID-13', 'Falla', 'falla@uco.test', '3000000013', 'ACTIVO')"
ejec usuarios "INSERT INTO asesor_ficha (usuario_id) VALUES ('$U13')"
ejec fichas "ALTER TABLE asesor_ficha ADD CONSTRAINT chk_prueba CHECK (nombre <> 'Falla') NOT VALID"
app -d fichas_perfil -c "SELECT * FROM sync.sincronizar_fichas()" >/dev/null 2>&1
codigo=$?
afirmar "debeRevertirTodo_cuandoUnaReplicaFalla" "1|Ana Nueva" \
    "$codigo|$(val fichas "SELECT nombre FROM estudiante WHERE id = '$U01'")"
ejec fichas "ALTER TABLE asesor_ficha DROP CONSTRAINT chk_prueba"

echo
echo "$((total - fallos))/$total pruebas verdes"
[[ $fallos -eq 0 ]]
