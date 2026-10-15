# Enums de catálogo

Referencia de `arquisoft-estandares`. Se carga cuando la HU crea, modifica o convierte un enum de
catálogo, o cuando la validación llega al Nivel 2.10.

## Enums de catálogo

`valueOf` nunca se llama fuera del propio enum. Cada enum expone `desde(String)` (devuelve la
constante o lanza su `{Enum}NoEncontradoException` → 422) y `getId()` devolviendo `name()`; si el
valor llega por el `crear(...)` de un domain, también `esValido(String)` para acumular en el
`ValidationResult`. Ambos delegan en `UtilEnum.desde(...)`. Los mappers persisten `getId()`, nunca
un `.name()` desnudo.

**El `String` del cliente viaja crudo y solo se convierte en el setter del domain** (referencia:
`TipoItem` en `ItemFichaPerfilDomain`). `RequestDTO`, `RequestMapper` y `{Accion}{Entidad}Mapper` lo
pasan tal cual; `Command.crear(...)` solo aplica `ValidatorTexto.noEnBlanco`, no la pertenencia al
catálogo.

```java
private void setTipoItem(String tipoItem, ValidationResult result) {
    if (!ValidatorTexto.noEnBlanco(tipoItem, /* ... */ result)) return;
    if (!TipoItem.esValido(tipoItem)) {
        result.agregarError(/* TIPO_ITEM_INVALIDO */);
        return;
    }
    this.tipoItem = TipoItem.desde(tipoItem);
}
```

Validar el catálogo antes, en el `Command` o el mapper, rompe la acumulación: `desde(...)` lanza al
primer error y el cliente pierde el resto de los `fieldErrors[]`.

**Camino inverso (BD → domain):** convierte el mapper de `secondaryport` antes de `reconstruir(...)`
(`EstadoFicha.desde(entity.estadoFicha())` en `EstadoFichaPerfilMapper.toDomain`). Ahí `desde(...)`
lanza directo: un id inválido en BD es un fallo de integridad, no un error de entrada.

**Las constantes se copian de `mer/data/{NN}_data_{contexto}.sql`** en `arquisoft-docs` (skill
`gh-docs-reader`), nunca se deducen del Event Storming ni del modelo enriquecido. Por fila: `id` es la
constante Java (UPPER_SNAKE_CASE, ADR-012), `nombre` es lo que devuelve `getNombre()` (por eso se
queda en Java, no en el catálogo Redis) y `descripcion` es solo documentación. El conjunto de filas
**es** el conjunto de constantes; la única sin fila es el centinela `VACIO`, que nunca se persiste.

**Dónde vive un enum de catálogo es una decisión abierta**: coexisten `domain/{catalogo}/` (con tabla
propia: `EstadoFicha`, `TipoItem`, `EstadoEvaluacion`) y `domain/{feature}/model/`. Un enum nuevo
sigue lo que ya use su contexto.

### Cuando infrastructure necesita nombrar un enum de dominio

No puede importarlo: se espeja con un enum propio de infraestructura que carga el código como texto,
y el `Command.crear(...)` lo resuelve contra el del dominio (`RolUsuarioDTO`, `TipoNotificacionEvento`).
Un enum espejo en vez de constantes sueltas da un único sitio y **una** prueba de deriva en las dos
direcciones.

| Espejo | Dónde vive | Para qué |
|---|---|---|
| `TipoNotificacionEvento` | `primaryadapter/amqp/` | el código que el consumidor pone en el `Command` |
| `EstadoNotificacionPersistencia` | `secondaryadapter/repository/` | el valor de la columna en una consulta del adapter |

El nombre dice para qué lado se espeja; vive en el paquete del adaptador que lo usa, no en un
`config/` común; y declara **todas** las constantes del dominio, no solo la usada hoy, para que la
prueba detecte un valor nuevo sin espejar. Ver `TipoNotificacionEventoTest` y
`EstadoNotificacionPersistenciaTest`.
