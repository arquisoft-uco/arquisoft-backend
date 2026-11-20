#!/usr/bin/env bash
set -euo pipefail

DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
RAIZ=$(dirname "$DIR")

SYNC_PORT=${SYNC_PORT:-5432}
SYNC_PSQL=${SYNC_PSQL:-psql -U postgres -X -q -v ON_ERROR_STOP=1}

declare -A BASE=(
    [usuarios]=usuarios
    [fichas]=fichas_perfil
    [proyectos]=proyectos_grado
    [artefactos]=artefactos
    [entregables]=entregables
    [evaluaciones]=evaluaciones
    [solicitudes]=solicitudes
    [biblioteca]=biblioteca
    [repositorio_artefactos]=repositorio_artefactos
    [mapas_ruta]=mapas_ruta
)

declare -A ORIGENES=(
    [fichas]="usuarios"
    [solicitudes]="usuarios"
    [repositorio_artefactos]="usuarios"
    [proyectos]="usuarios entregables"
    [artefactos]="usuarios proyectos repositorio_artefactos"
    [entregables]="usuarios proyectos"
    [biblioteca]="usuarios proyectos"
    [mapas_ruta]="usuarios proyectos"
    [evaluaciones]="usuarios proyectos entregables"
)

declare -A TABLAS=(
    [usuarios]="usuario estudiante asesor asesor_ficha coordinador representante_comite_curriculum administrador bibliotecario jurado"
    [proyectos]="proyecto_grado estado_proyecto_grado estudiante_proyecto_grado asesor_proyecto_grado coordinador estudiante"
    [entregables]="entregable_proyecto_grado estado_entregable_proyecto_grado proyecto_grado"
    [repositorio_artefactos]="version_repositorio_artefacto repositorio_artefacto"
)

TODOS=(fichas proyectos artefactos entregables evaluaciones solicitudes biblioteca repositorio_artefactos mapas_ruta)

fallar() {
    echo "preparar.sh: $*" >&2
    exit 1
}

[[ -n "${SYNC_LECTOR_PASSWORD:-}" ]] || fallar "falta SYNC_LECTOR_PASSWORD"
[[ -n "${SYNC_HOST:-}" ]] || fallar "falta SYNC_HOST (alias de red del contenedor de Postgres, p. ej. arquisoft-postgres)"
[[ "$SYNC_PORT" =~ ^[0-9]+$ ]] || fallar "SYNC_PORT debe ser numérico"
[[ "$SYNC_LECTOR_PASSWORD" != *"'"* && "$SYNC_LECTOR_PASSWORD" != *'\'* && "$SYNC_LECTOR_PASSWORD" != *$'\n'* ]] \
    || fallar "SYNC_LECTOR_PASSWORD no puede contener comilla simple, barra invertida ni saltos de línea"

host_minuscula=${SYNC_HOST,,}
case "$host_minuscula" in
    localhost | localhost.* | 127.* | ::1 | 0.0.0.0 | ip6-localhost | ip6-loopback)
        fallar "SYNC_HOST no puede ser loopback ($SYNC_HOST): pg_hba aplica 'trust' a loopback y postgres_fdw exige contraseña efectiva. Usa el alias de red del contenedor."
        ;;
esac

[[ "$SYNC_HOST" =~ ^[A-Za-z0-9._-]+$ ]] || fallar "SYNC_HOST solo admite letras, dígitos, punto, guion y guion bajo"

if [[ -n "${SYNC_DESTINOS:-}" ]]; then
    read -r -a destinos <<< "${SYNC_DESTINOS//,/ }"
    for d in "${destinos[@]}"; do
        [[ -n "${ORIGENES[$d]:-}" ]] || fallar "contexto destino desconocido: $d (válidos: ${TODOS[*]})"
    done
else
    destinos=("${TODOS[@]}")
fi

ejecutar() {
    local base=$1
    shift
    {
        printf '\\set sync_password '"'"'%s'"'"'\n' "$SYNC_LECTOR_PASSWORD"
        printf '\\set sync_host '"'"'%s'"'"'\n' "$SYNC_HOST"
        printf '\\set sync_port '"'"'%s'"'"'\n' "$SYNC_PORT"
        for extra in "$@"; do
            printf '%s\n' "$extra"
        done
        cat
    } | $SYNC_PSQL -d "$base" -f -
}

echo "preparar.sh: rol sync_lector"
ejecutar postgres <<'SQL'
SELECT format('%s ROLE sync_lector LOGIN PASSWORD %L',
              CASE WHEN EXISTS (SELECT FROM pg_roles WHERE rolname = 'sync_lector') THEN 'ALTER' ELSE 'CREATE' END,
              :'sync_password') \gexec
SQL

declare -A necesarios=()
for d in "${destinos[@]}"; do
    for o in ${ORIGENES[$d]}; do
        necesarios[$o]=1
    done
done

for o in "${!necesarios[@]}"; do
    echo "preparar.sh: permisos de lectura en ${BASE[$o]}"
    tablas=${TABLAS[$o]// /,}
    ejecutar "${BASE[$o]}" "\\set tablas '$tablas'" < "$DIR/origen.sql"
done

for d in "${destinos[@]}"; do
    echo "preparar.sh: ${BASE[$d]} (sync.sincronizar_$d)"
    cadena=""
    for o in ${ORIGENES[$d]}; do
        cadena+="${cadena:+;}$o|${BASE[$o]}|${TABLAS[$o]// /,}"
    done
    cat "$DIR/destino.sql" "$RAIZ/$d.sql" | ejecutar "${BASE[$d]}" "\\set origenes '$cadena'"
done

echo "preparar.sh: listo"
