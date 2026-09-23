# Enums de catálogo

Referencia de `arquisoft-estandares`. Se carga cuando la HU crea, modifica o convierte un enum de
catálogo, o cuando la validación llega al Nivel 2.10.

## Enums de catálogo

`valueOf` **nunca** se llama fuera del propio enum. Cada enum expone `desde(String)` (devuelve la
constante o lanza su `{Enum}NoEncontradoException` → 422) y `getId()` devolviendo `name()`; si el
valor llega por el `crear(...)` de un domain, expone además `esValido(String)` para acumular en el
`ValidationResult` en vez de abortar al primer error. Ambos delegan en `UtilEnum.desde(...)`.
Los mappers persisten `getId()`, nunca un `.name()` desnudo.

**Dónde se convierte el `String` que manda el cliente.** El id viaja crudo por toda la cadena y solo
se convierte dentro del `crear(...)` del domain. Referencia: `TipoItem` en `ItemFichaPerfilDomain`.

| Capa | Qué hace con el `String` |
|---|---|
| `{Accion}{Entidad}RequestDTO` | lo lleva como `String`, sin anotaciones |
| `{Accion}{Entidad}RequestMapper` | lo pasa tal cual al `Command` |
| `{Accion}{Entidad}Command.crear(...)` | solo `ValidatorTexto.noEnBlanco` — **no** valida pertenencia al catálogo |
| `{Accion}{Entidad}Mapper.toDomain(...)` | lo pasa tal cual al `crear(...)` del domain |
| **`{Entidad}Domain`**, setter privado | **`esValido(...)` acumula en el `ValidationResult`; `desde(...)` convierte** |

```java
private void setTipoItem(String tipoItem, ValidationResult result) {
    if (!ValidatorTexto.noEnBlanco(tipoItem, /* ... */ result)) return;
    if (!TipoItem.esValido(tipoItem)) {                    // acumula, no lanza
        result.agregarError(/* TIPO_ITEM_INVALIDO */);
        return;
    }
    this.tipoItem = TipoItem.desde(tipoItem);              // conversión real
}
```

El campo es del tipo del enum; el parámetro del setter es `String`. Validar el catálogo en el
`Command` o en el `{Accion}{Entidad}Mapper` rompe la acumulación: `desde(...)` lanza al primer error
y el cliente pierde el resto de los `fieldErrors[]`.

**Camino inverso (fila de BD → domain):** ahí sí convierte el mapper de `secondaryport`, antes de
`reconstruir(...)` — `EstadoFicha.desde(entity.estadoFicha())` en `EstadoFichaPerfilMapper.toDomain`.
`reconstruir(...)` recibe el enum ya tipado y no acumula: un id inválido guardado en BD es un fallo
de integridad del catálogo, no un error de entrada, así que `desde(...)` lanza directo.

**Sus constantes no se inventan ni se deducen del Event Storming: se copian de
`mer/data/{NN}_data_{contexto}.sql` en `arquisoft-docs`** (ver la skill `gh-docs-reader`). Ese
archivo es la fuente de verdad y define fila por fila las tres cosas que necesitas: `id` es la
constante Java (`Enum.name()`, UPPER_SNAKE_CASE, ADR-012), `nombre` es la etiqueta que devuelve
`getNombre()` — por eso se queda en Java y no va al catálogo Redis, su fuente de verdad es esa
fila— y `descripcion` es solo documentación del MER. El conjunto de filas **es** el conjunto de
constantes; la única que existe sin fila es el centinela `VACIO`, artefacto del código que nunca se
persiste. Agregar un estado que el modelo enriquecido menciona pero el `data/` no tiene ya pasó una
vez y hubo que revertirlo.

**Dónde vive un enum de catálogo es una decisión abierta del proyecto** — hoy coexisten
`domain/{catalogo}/` (cuando tiene tabla propia: `EstadoFicha`, `TipoItem`, `EstadoEvaluacion`) y
`domain/{feature}/model/` (cuando no la tiene). Un enum nuevo sigue lo que ya use su contexto; no
declares "settled" una convención que no lo está.

### Cuando infrastructure necesita nombrar un enum de dominio

No puede importarlo — la barrera de capas lo prohíbe. Se espeja: un enum propio en infraestructura
que carga el código como texto, y el `Command.crear(...)` lo resuelve contra el catálogo del dominio.
Es lo que hacen `RolUsuarioDTO` (usuarios, porque además es el contrato JSON) y `TipoNotificacionEvento`
(notificaciones, `primaryadapter/amqp/`).

**Una tabla espejo, no una constante por clase.** Con un solo consumidor una `private static final
String` basta; con seis son seis literales sueltos y seis pruebas de deriva. El enum da un sitio
único donde ver qué valores existen y **una** prueba que cubre las dos direcciones: que cada código
resuelva con `desde(...)`, y que ambos enums declaren el mismo conjunto de constantes — así, si el
dominio gana un valor y nadie lo espeja, el build falla.

Hay dos espejos hoy y el nombre lleva **para qué lado** se espeja, no solo qué:

| Espejo | Dónde vive | Para qué |
|---|---|---|
| `TipoNotificacionEvento` | `primaryadapter/amqp/` | el código que el consumidor pone en el `Command` |
| `EstadoNotificacionPersistencia` | `secondaryadapter/repository/` | el valor de la columna en una consulta del adapter |

Cada uno vive **en el paquete del adaptador que lo usa**, no en un `config/` común: es un detalle de
ese adaptador, no del contexto. Y declara **todas** las constantes del enum de dominio, no solo la
que se usa hoy — con una sola, la prueba no detectaría que el dominio ganó un valor que la
infraestructura ignora, que es precisamente la deriva que se quiere cazar. Ver
`TipoNotificacionEventoTest` y `EstadoNotificacionPersistenciaTest`.


