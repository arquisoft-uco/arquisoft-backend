# Query Object + Spring Data Specification — Filtros Dinámicos con Agrupación Booleana

Este documento describe la implementación del sistema de filtrado dinámico del proyecto,
que combina dos patrones arquitectónicos complementarios:

- **Query Object** (Martin Fowler, POEAA): representa la consulta como un objeto portable
  e independiente de la tecnología de persistencia — aplicado en capas de dominio y aplicación.
- **Spring Data Specification**: traduce ese objeto a predicados JPA — aplicado exclusivamente
  en la capa de infraestructura.

---

## Índice

1. [Objetivo](#1-objetivo)
2. [Visión general de la arquitectura](#2-visión-general-de-la-arquitectura)
3. [Módulos involucrados](#3-módulos-involucrados)
4. [Estructura de carpetas](#4-estructura-de-carpetas)
5. [Clases e interfaces — descripción](#5-clases-e-interfaces--descripción)
   - [shared:query — vocabulario de consulta sin Spring](#51-sharedquery--vocabulario-de-consulta-sin-spring)
   - [shared:jpa — capa de infraestructura compartida](#52-sharedjpa--capa-de-infraestructura-compartida)
   - [shared:query — DTOs de transporte HTTP](#53-sharedquery--dtos-de-transporte-http)
   - [fichas:application — criterio concreto](#54-fichasapplication--criterio-concreto)
   - [fichas:infrastructure — adaptadores concretos](#55-fichasinfrastructure--adaptadores-concretos)
6. [Flujo de una solicitud](#6-flujo-de-una-solicitud)
7. [Árbol de filtros — modelo de datos](#7-árbol-de-filtros--modelo-de-datos)
8. [Operadores por tipo de dato](#8-operadores-por-tipo-de-dato)
9. [Principios SOLID aplicados](#9-principios-solid-aplicados)
10. [Cómo extender a un nuevo contexto](#10-cómo-extender-a-un-nuevo-contexto)

---

## 1. Objetivo

Proveer un mecanismo genérico, reutilizable y alineado con SOLID para que cualquier
bounded context pueda exponer consultas filtrables, ordenables y paginadas sin duplicar
lógica. Un sistema externo puede construir la consulta completa —incluyendo agrupaciones
booleanas complejas— y enviarla como JSON al endpoint correspondiente.

**Patrón Query Object** (dominio y aplicación): encapsula todos los parámetros de una
consulta —paginación, ordenamiento y árbol de filtros— en un objeto inmutable tipado.
El objeto existe sin saber nada de JPA, SQL ni Spring. Es portable, testeable y reutilizable
en cualquier tipo de persistencia.

**Spring Data Specification** (infraestructura): recibe ese objeto y lo traduce a
predicados `javax.persistence.criteria.Predicate` usando la API `Specification<T>` de
Spring Data JPA. Es el único punto del sistema que conoce JPA.

---

## 2. Visión general de la arquitectura

```
 HTTP Client
     │
     ▼
 QueryCriteriaRequestDTO   ← JSON deserializado (shared:web)
     │  .parsearFiltros()
     │  .parsearOrdenamiento()
     ▼
 FichaPerfilCriteria       ← [QUERY OBJECT] criteria concreto (fichas:application)
     │  extends QueryCriteria
     │
     ▼
 ConsultarFichasPerfilUseCase  ← orquestación (fichas:application)
     │
     ▼
 FichaPerfilQueryOutputPort    ← puerto de salida (fichas:application)
     │
     ▼
 FichaPerfilQueryOutputAdapter ← adaptador JPA (fichas:infrastructure) — pura delegación:
     │  PageableMapper.toPageable(criteria, sortMapper) + specification.desdeCriteria(criteria)
     ▼
 FichaPerfilJpaSpecification   ← [SPRING DATA SPECIFICATION] spec concreta (fichas:infrastructure)
     │  extends QueryJpaSpecification<FichaPerfilJpaQueryEntity>
     │
     ▼
 QueryJpaSpecification<E>      ← recorre NodoFiltro (shared:jpa)
     │  camposPermitidos().get(campo).construirSpec(operador, valor)
     ▼
 CampoSpec<E>                  ← predicado JPA por tipo (shared:jpa)
     │
     ▼
 Specification<FichaPerfilJpaQueryEntity>  → SQL WHERE generado
```

`FichaPerfilJpaQueryEntity` es la entidad de lectura dedicada de `fichaperfil` (`@Subselect`,
ver CLAUDE.md → "Read-side entities"), **plana** — sin `@ManyToOne` — porque el `@Subselect`
ya resuelve el join `ficha_perfil ⋈ asesor_ficha` en SQL. Por eso `FichaPerfilJpaSpecification`
direcciona campos como `root.get("asesorNombre")`, nunca `root.get("asesorFicha").get("nombre")`.

La frontera entre los dos patrones está exactamente en `FichaPerfilQueryOutputPort`:
todo lo que está por encima es **Query Object**; todo lo que está por debajo es
**Spring Data Specification**.

---

## 3. Módulos involucrados

| Módulo | Capa | Patrón | Responsabilidad |
|--------|------|--------|----------------|
| `shared:query` | Vocabulario de consulta, sin Spring | Query Object | Modelo del árbol de filtros, operadores, conector, criteria base, Template Method de validación, DTOs de deserialización JSON, paginación (`PaginatedResult`/`SortDirection`) |
| `shared:jpa` | Infraestructura compartida | Spring Data Specification | Traducción del árbol a predicados JPA, validación de tipos, `PageableMapper`/`PaginationMapper` |
| `{ctx}:application` | Aplicación | Query Object | Criteria concreto del contexto (enum de campos + builder con hooks) |
| `{ctx}:infrastructure` | Infraestructura | Spring Data Specification | Spec JPA concreta con el mapa de campos filtrables; mapper de sort; entidad de lectura dedicada (`{Feature}JpaQueryEntity`) |

`shared:query` no tiene dependencia de Spring en absoluto (ver CLAUDE.md → "shared:query owns
the entire read-side vocabulary") — solo `shared:exception`/`shared:message` y `shared:util`.
`shared:jpa` sí depende de Spring Data JPA y de `shared:query` (`api project(':shared:query')`),
nunca al revés.

> `{ctx}` = cualquier bounded context (`fichas`, `proyectos`, `evaluaciones`, etc.)

---

## 4. Estructura de carpetas

```
shared/
├── query/src/main/java/com/arquisoft/shared/query/       ← [QUERY OBJECT] — sin Spring
│   ├── FiltroOperador.java                 ← enum de operadores de comparación
│   ├── FiltroConector.java                 ← enum AND / OR
│   ├── NodoFiltro.java                     ← sealed interface: árbol booleano
│   ├── SortOrder.java                      ← valor de ordenamiento (campo + dirección)
│   ├── QueryCriteria.java                  ← clase abstracta base para criterios
│   ├── exception/
│   │   ├── FiltroException.java            ← excepción de dominio para validaciones del filtro
│   │   └── FiltroInvalidoException.java    ← campo/valor inválido detectado al traducir a JPA
│   ├── dto/
│   │   ├── NodoFiltroDTO.java              ← interface Jackson con @JsonTypeInfo
│   │   ├── PredicadoFiltroDTO.java         ← DTO para nodos hoja
│   │   ├── GrupoFiltroDTO.java             ← DTO para nodos internos
│   │   └── QueryCriteriaRequestDTO.java    ← body completo del endpoint POST
│   └── pagination/
│       ├── PaginatedResult.java            ← resultado paginado de salida
│       ├── PaginationRequest.java
│       └── SortDirection.java              ← ASC / DESC
│
└── jpa/src/main/java/com/arquisoft/shared/jpa/           ← [SPRING DATA SPECIFICATION]
    ├── query/
    │   ├── CampoSpec.java                  ← sealed interface: predicado JPA por tipo
    │   └── QueryJpaSpecification.java      ← abstract: recorre el árbol y compone specs
    ├── repository/
    │   ├── QueryRepository.java            ← @NoRepositoryBean, solo findById/existsById/findAll/count
    │   └── SpecificationQueryRepository.java  ← + Specification<T>
    └── util/
        ├── PageableMapper.java             ← QueryCriteria → Pageable
        └── PaginationMapper.java           ← Page<T> → PaginatedResult<T>

fichas/
├── application/src/main/java/com/arquisoft/fichas/application/fichaperfil/query/
│   └── criteria/
│       └── FichaPerfilCriteria.java        ← [QUERY OBJECT] criteria concreto con enum de campos
│
└── infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/fichaperfil/query/
    ├── primaryadapter/web/
    │   ├── ConsultarFichasPerfilController.java        ← endpoint POST
    │   └── mapper/
    │       └── ConsultarFichasPerfilRequestMapper.java ← QueryCriteriaRequestDTO → FichaPerfilCriteria
    └── secondaryadapter/repository/
        ├── FichaPerfilJpaQueryEntity.java              ← entidad de lectura dedicada (@Subselect, plana)
        ├── FichaPerfilJpaSpecification.java            ← [SPRING DATA SPECIFICATION] mapa de campos
        ├── FichaPerfilSortMapper.java                  ← traduce claves lógicas a rutas JPA para sort
        ├── FichaPerfilQueryOutputAdapter.java          ← ejecuta la consulta JPA (pura delegación)
        └── mapper/
            └── FichaPerfilQueryMapper.java              ← FichaPerfilJpaQueryEntity → FichaPerfilReadModel
```

`FiltroInvalidoException` vive en `shared:query` (agrupada junto a `FiltroException` — ambas son
errores de consulta mal formada, 400) aunque quien la lanza es `CampoSpec` en `shared:jpa`, que es
el único módulo con visibilidad del tipo Java destino (`UUID`, `LocalDate`, ...) para saber si el
valor recibido no parsea.

---

## 5. Clases e interfaces — descripción

### 5.1 `shared:query` — vocabulario de consulta sin Spring

> Módulo dedicado, sin dependencia de Spring, JPA ni de ningún otro módulo `shared` salvo
> `shared:exception`/`shared:message` (`api`) y `shared:util`. Usable en cualquier capa —
> incluida `domain` si algún contexto lo necesitara. Todas las clases de este módulo
> pertenecen al **patrón Query Object**. Antes vivía repartido entre `shared:domain`,
> `shared:web` y `shared:postgres`; se extrajo a su propio módulo precisamente para que
> ningún consumidor que solo necesite declarar un criterio arrastre Spring Data JPA.

---

#### `FiltroOperador` — enum

Conjunto cerrado de operadores de comparación soportados por el sistema.

| Grupo | Operadores |
|-------|-----------|
| Texto | `CONTIENE` `NO_CONTIENE` `EMPIEZA_CON` `TERMINA_CON` |
| Igualdad (todos los tipos) | `ES` `NO_ES` |
| Comparación (números y fechas) | `MAYOR_QUE` `MENOR_QUE` `MAYOR_IGUAL_QUE` `MENOR_IGUAL_QUE` |
| Nulidad (sin valor) | `ES_NULO` `NO_ES_NULO` |

Método clave: `requiereValor()` — indica si el operador necesita un valor o no.
Factory: `parse(String)` — convierte el string del JSON al enum con mensaje de error
descriptivo; se invoca explícitamente en el DTO para evitar que Jackson use su
propio mecanismo de deserialización de enums.

---

#### `FiltroConector` — enum

Define cómo se combinan los nodos hermanos dentro de un `Grupo`.

| Valor | Semántica SQL |
|-------|--------------|
| `AND` | Todos los nodos hijos deben cumplirse |
| `OR` | Basta con que uno de los hijos se cumpla |

Factory: `parse(String)` — mismo comportamiento que `FiltroOperador.parse`.

---

#### `FiltroException` — excepción de consulta

Extiende `ApplicationException` (HTTP 400) — **nunca `DomainException`**: una consulta mal
formada es una petición inválida, no una violación de una regla de negocio; devolver 422
le diría al cliente que su payload se entendió semánticamente cuando no fue así. Se lanza
dentro de `shared:query` cuando:

- Un campo de filtro no está en los campos permitidos del contexto.
- Un campo de ordenamiento no está en los campos ordenables del contexto.
- Se usa un operador que requiere valor pero el valor es nulo o vacío.
- El árbol de filtros supera la profundidad máxima (`MAX_PROFUNDIDAD_FILTRO = 10`).

`shared:query` sigue sin ninguna dependencia de Spring — la resolución de texto pasa por
`Mensajes` (fachada estática de `shared:message`), igual que en el dominio.

---

#### `NodoFiltro` — sealed interface

Árbol de expresión booleana. Tiene exactamente dos implementaciones:

```
NodoFiltro
├── Predicado(campo, operador, valor)   ← nodo hoja: condición atómica
└── Grupo(conector, List<NodoFiltro>)   ← nodo interno: agrupa con AND u OR
```

**`Predicado`**

- `campo` — nombre lógico del campo (e.g. `"tituloProyecto"`)
- `operador` — `FiltroOperador` a aplicar
- `valor` — valor en texto para comparar; `null` cuando el operador no lo requiere

**`Grupo`**

- `conector` — `AND` u `OR`, aplica entre **todos** sus hijos
- `nodos` — lista inmutable de `NodoFiltro` (puede contener `Predicado` y/o `Grupo` anidados)

Los grupos pueden anidarse a cualquier profundidad. El motor JPA genera los paréntesis
SQL automáticamente al componer los predicados.

---

#### `SortOrder` — value object

Representa un criterio de ordenamiento: campo + dirección (`ASC` o `DESC`).

- `parse("campo:ASC")` — factory desde string (formato del request)
- `parse("campo")` — dirección por defecto: `ASC`

---

#### `QueryCriteria` — abstract class

Clase base inmutable para todos los criterios de consulta del proyecto.
Encapsula paginación, ordenamiento y la raíz del árbol de filtros.

```java
public abstract class QueryCriteria {
    int pagina
    int tamanio
    List<SortOrder> ordenamiento
    NodoFiltro raiz          // null = sin filtros
}
```

**`BaseBuilder<B>`** — Template Method con self-type para subclases fluidas.

El builder implementa las validaciones comunes en sus métodos `ordenamiento()` y
`raiz()`, y delega en dos métodos hook para conocer los campos permitidos de cada
contexto concreto:

```java
protected Set<String> camposFiltrables() { return null; }  // null = sin restricción
protected Set<String> camposOrdenables() { return null; }  // null = sin restricción
```

Las subclases sobreescriben estos hooks para declarar qué campos acepta su contexto.
Cuando se llama a `.ordenamiento(...)`, el builder valida cada `SortOrder` contra
`camposOrdenables()`. Cuando se llama a `.raiz(...)`, valida recursivamente cada
`NodoFiltro.Predicado` contra `camposFiltrables()` y verifica que los operadores que
requieren valor tengan uno no vacío. Todas las violaciones lanzan `FiltroException`.

```java
FichaPerfilCriteria.builder()
    .pagina(0)
    .tamanio(10)
    .ordenamiento(List.of(SortOrder.parse("tituloProyecto:ASC")))
    .raiz(NodoFiltro.predicado("tituloProyecto", FiltroOperador.CONTIENE, "web"))
    .build()
```

---

### 5.2 `shared:jpa` — capa de infraestructura compartida

> Usa Spring Data JPA (`Specification`, `Root`, `CriteriaBuilder`). No debe importarse
> desde capas de dominio ni de aplicación.
> Todas las clases de este paquete pertenecen al **patrón Spring Data Specification**.

`FiltroInvalidoException` (campo inexistente en el mapa de campos permitidos, operador
incompatible con el tipo, o valor que no parsea) se lanza desde `CampoSpec` — pero la clase
en sí vive en `shared:query`, no aquí (ver "Estructura de carpetas" arriba).

---

#### `CampoSpec<E>` — sealed interface

**El corazón del sistema de traducción JPA.** Define el contrato para construir un predicado
JPA dado un operador y un valor en texto.

```java
Specification<E> construirSpec(FiltroOperador operador, String valor);
```

Cada implementación sealed cubre **un único tipo de dato** y es responsable de:
1. Validar que el operador sea compatible con su tipo.
2. Parsear el `valor` string al tipo Java correspondiente.
3. Construir el predicado JPA usando `CriteriaBuilder`.

| Implementación | Tipo Java | Operadores válidos |
|----------------|-----------|-------------------|
| `CampoSpec.Texto<E>` | `String` | Todos excepto comparación numérica |
| `CampoSpec.Uuid<E>` | `UUID` | `ES` `NO_ES` `ES_NULO` `NO_ES_NULO` |
| `CampoSpec.Entero<E>` | `Long` | Igualdad + comparación + nulidad |
| `CampoSpec.Decimal<E>` | `BigDecimal` | Igualdad + comparación + nulidad |
| `CampoSpec.Fecha<E>` | `LocalDate` | Igualdad + comparación + nulidad |
| `CampoSpec.FechaHora<E>` | `LocalDateTime` | Igualdad + comparación + nulidad |
| `CampoSpec.Booleano<E>` | `Boolean` | `ES` `NO_ES` `ES_NULO` `NO_ES_NULO` |

**Factories estáticos** para construcción legible en el mapa de campos:

```java
CampoSpec.texto(root -> root.get("tituloProyecto"))
CampoSpec.uuid(root -> root.get("asesorId"))
CampoSpec.fecha(root -> root.get("fechaCreacion"))
CampoSpec.entero(root -> root.get("anio"))
```

Los `root.get(...)` son siempre de un solo nivel — nunca `root.get("x").get("y")` — porque
la entidad `E` es la `{Feature}JpaQueryEntity` de lectura (`@Subselect`, ver 5.5), que ya
llega plana: el join se resolvió en el SQL del subselect, no aquí.

---

#### `QueryJpaSpecification<E>` — abstract class

Recorre recursivamente el árbol `NodoFiltro` y compone una `Specification<E>` JPA.

**Método público:**
```java
public final Specification<E> desdeCriteria(QueryCriteria criteria)
```
- Si `criteria` no tiene filtros → devuelve predicado neutro (sin WHERE).
- Si tiene filtros → delega a `especDesdeNodo(raiz)`.

**Método a implementar por subclases:**
```java
protected abstract Map<String, CampoSpec<E>> camposPermitidos();
```
El mapa asocia el nombre lógico del campo (tal como llega en el JSON) con su `CampoSpec`
correspondiente. Es el único punto de extensión para cada contexto.

**Algoritmo de recorrido:**

```
especDesdeNodo(nodo):
  si nodo es Predicado → busca campo en camposPermitidos(), delega a CampoSpec
  si nodo es Grupo     → acumula specs con .and() / .or() según conector
```

La recursión garantiza que los grupos anidados generen los paréntesis SQL correctos,
ya que Spring Data JPA parentesiza automáticamente los predicados compuestos.

---

### 5.3 `shared:query` — DTOs de transporte HTTP

> Usan Jackson y Lombok — las únicas clases de `shared:query` con esas dependencias.
> Antes vivían en `shared:web/dto/query/`; se movieron aquí junto al resto del vocabulario
> de consulta, ya que son la forma serializable de `NodoFiltro`/`QueryCriteria`, no algo
> genérico de transporte HTTP.

---

#### `NodoFiltroDTO` — interface Jackson

Contrato de deserialización para el árbol de filtros. Usa `@JsonTypeInfo` con
discriminador `"tipo"` para instanciar la implementación correcta.

```json
{ "tipo": "PREDICADO", ... }   →  PredicadoFiltroDTO
{ "tipo": "GRUPO",     ... }   →  GrupoFiltroDTO
```

Método único: `toDomain()` — convierte el DTO al modelo de dominio `NodoFiltro`.

---

#### `PredicadoFiltroDTO`

DTO para nodos hoja. Campos: `campo` (`String`), `operador` (`String`), `valor` (nullable).

El campo `operador` es `String` deliberadamente: Jackson puede deserializar enums
directamente, pero lo haría por ordinal o nombre sin pasar por `FiltroOperador.parse()`,
perdiendo el mensaje de error descriptivo. `toDomain()` llama `FiltroOperador.parse(operador)`
explícitamente para garantizar el error correcto ante valores inválidos.

---

#### `GrupoFiltroDTO`

DTO para nodos internos. Campos: `conector` (`String`), `nodos` (`List<NodoFiltroDTO>`).
La lista es polimórfica: cada elemento puede ser `PREDICADO` o `GRUPO`, lo que
permite el anidamiento recursivo en la deserialización.

El campo `conector` es `String` por la misma razón que `operador` en `PredicadoFiltroDTO`.
`toDomain()` llama `FiltroConector.parse(conector)` explícitamente.

---

#### `QueryCriteriaRequestDTO`

Body completo del endpoint de consulta. Reutilizable en cualquier contexto.

```json
{
  "pagina": 0,
  "tamanio": 10,
  "ordenamiento": ["campo:ASC"],
  "filtros": { <NodoFiltroDTO> }
}
```

Métodos de conversión:
- `parsearOrdenamiento()` → `List<SortOrder>`
- `parsearFiltros()` → `NodoFiltro` (o `null` si no hay filtros)

---

### 5.4 `fichas:application` — criterio concreto

---

#### `FichaPerfilCriteria` — [QUERY OBJECT]

Criteria concreto para el contexto fichas. Declara un enum `Campo` que centraliza
todos los campos del contexto —tanto filtrables como ordenables— con flags booleanos
para cada uso. El `Builder` sobreescribe los hooks del `BaseBuilder` para activar la
validación automática.

```java
public final class FichaPerfilCriteria extends QueryCriteria {

    public enum Campo {
        TITULO_PROYECTO("tituloProyecto", true,  true),
        ASESOR_NOMBRE  ("asesorNombre",   true,  true),
        ASESOR_EMAIL   ("asesorEmail",    true,  true),
        ASESOR_ID      ("asesorId",       true,  false);  // filtrable, NO ordenable

        // Sets derivados del enum — O(1) lookup en validaciones
        static final Set<String> CLAVES_FILTRABLES = ...;
        static final Set<String> CLAVES_ORDENABLES = ...;

        public static boolean esValidoParaFiltrar(String clave) { ... }
        public static boolean esValidoParaOrdenar(String clave) { ... }
    }

    public static Builder builder() { return new Builder(); }

    public static final class Builder extends QueryCriteria.BaseBuilder<Builder> {
        @Override protected Set<String> camposFiltrables() { return Campo.CLAVES_FILTRABLES; }
        @Override protected Set<String> camposOrdenables() { return Campo.CLAVES_ORDENABLES; }
        public FichaPerfilCriteria build() { return new FichaPerfilCriteria(this); }
    }
}
```

El enum `Campo` es la **fuente de verdad** del contexto: toda la infraestructura
(`FichaPerfilJpaSpecification`, `FichaPerfilSortMapper`) itera sobre sus valores
usando switches exhaustivos, de modo que agregar un nuevo campo implica solo extender
el enum y el compilador señala todos los puntos que requieren actualización.

---

### 5.5 `fichas:infrastructure` — adaptadores concretos

---

#### `FichaPerfilJpaQueryEntity` — `@Entity` `@Immutable` `@Subselect`

Entidad de lectura dedicada del feature (ver CLAUDE.md → "Read-side entities"). Resuelve
el join `ficha_perfil ⋈ asesor_ficha` en el `@Subselect` y expone columnas **planas**
(`tituloProyecto`, `asesorNombre`, `asesorEmail`, `asesorId`) — sin `@ManyToOne`. Es el
tipo `E` que parametriza tanto `FichaPerfilJpaSpecification` como el `FichaPerfilQueryRepository`.

---

#### `FichaPerfilJpaSpecification` — `@Component` — [SPRING DATA SPECIFICATION]

Extiende `QueryJpaSpecification<FichaPerfilJpaQueryEntity>`. Construye el mapa de campos
filtrables iterando `FichaPerfilCriteria.Campo` con un switch exhaustivo: si se agrega
un campo al enum sin actualizar el switch, el compilador falla.

```java
static {
    for (FichaPerfilCriteria.Campo campo : FichaPerfilCriteria.Campo.values()) {
        CampoSpec<FichaPerfilJpaQueryEntity> spec = switch (campo) {
            case TITULO_PROYECTO -> CampoSpec.texto(root -> root.get("tituloProyecto"));
            case ASESOR_NOMBRE   -> CampoSpec.texto(root -> root.get("asesorNombre"));
            case ASESOR_EMAIL    -> CampoSpec.texto(root -> root.get("asesorEmail"));
            case ASESOR_ID       -> CampoSpec.uuid(root -> root.get("asesorId"));
        };
        m.put(campo.getClave(), spec);
    }
}
```

Los `root.get(...)` son de un solo nivel, no `root.get("asesorFicha").get("nombre")` — la
entidad ya llega plana desde el `@Subselect` (ver `FichaPerfilJpaQueryEntity` arriba).

---

#### `FichaPerfilSortMapper`

Clase de utilidad (final, sin instancias) que traduce claves lógicas de ordenamiento
(`"tituloProyecto"`) a rutas de propiedad JPA (`"asesorNombre"` — también plana, mismo
motivo). También itera `FichaPerfilCriteria.Campo` con un switch exhaustivo y devuelve
`null` para los campos declarados como no ordenables; `PageableMapper.toPageable`
(`shared:jpa`) recibe esta función como `traductorDeCampo` y es quien lanza al recibir
`null` — el `SortMapper` no lanza nada por sí mismo.

---

#### `FichaPerfilQueryOutputAdapter` — `@Component`

Implementa `FichaPerfilQueryOutputPort`. Es **pura delegación** — no construye `Pageable`
a mano ni atrapa excepciones de Spring Data JPA:

```java
@Override
public PaginatedResult<FichaPerfilReadModel> consultarTodas(FichaPerfilCriteria criteria) {
    Pageable pageable = PageableMapper.toPageable(criteria, FichaPerfilSortMapper::traducir);
    Specification<FichaPerfilJpaQueryEntity> spec = specification.desdeCriteria(criteria);

    return PaginationMapper.toResult(
            fichaPerfilRepository.findAll(spec, pageable)
                    .map(FichaPerfilQueryMapper::toReadModel));
}
```

`PageableMapper.toPageable` (`shared:jpa`) construye el `Pageable` a partir del criteria y
del `traductorDeCampo` recibido; `PaginationMapper.toResult` (`shared:jpa`) convierte el
`Page<T>` de Spring Data en el `PaginatedResult<T>` de `shared:query`. `FichaPerfilQueryMapper`
(sibling `mapper/`, `final`, constructor privado, `toReadModel` estático) convierte
`FichaPerfilJpaQueryEntity → FichaPerfilReadModel`.

---

#### `ConsultarFichasPerfilController` — `@RestController`

Endpoint REST que recibe el body de consulta. La conversión DTO → Criteria no ocurre en el
controller: delega a `ConsultarFichasPerfilRequestMapper.toCriteria(request)`
(`primaryadapter/web/mapper/`), el contraparte de lectura del `{Action}{Entity}RequestMapper`
del lado comando.

```
POST /fichas-perfil/coordinador
Authorization: Bearer <token>     (requiere authority: ficha:ficha:view)
Content-Type: application/json

Body: QueryCriteriaRequestDTO     (opcional — sin body devuelve todo paginado)
```

---

## 6. Flujo de una solicitud

```
1. Cliente POST /fichas-perfil/coordinador  { "pagina":0, "tamanio":10, "filtros": {...} }

2. ConsultarFichasPerfilController
   └─ Deserializa QueryCriteriaRequestDTO via Jackson
   └─ ConsultarFichasPerfilRequestMapper.toCriteria(request)
        └─ QueryCriteriaRequestDTO.aplicarPorDefecto(dto)  ← body nulo → criteria vacío, no NPE
        └─ dto.parsearFiltros()  →  NodoFiltro (árbol) via NodoFiltroDTO.toDomain()
             └─ PredicadoFiltroDTO.toDomain() llama FiltroOperador.parse(operador)
        └─ dto.parsearOrdenamiento()  →  List<SortOrder>
        └─ FichaPerfilCriteria.builder()
             .ordenamiento(...)   ← BaseBuilder valida contra Campo.CLAVES_ORDENABLES
             .raiz(...)           ← BaseBuilder valida campos y operadores contra Campo.CLAVES_FILTRABLES
             .build()
   [Cualquier campo inválido lanza FiltroException aquí, antes de llegar al caso de uso]

3. ConsultarFichasPerfilUseCase.ejecutar(criteria)
   └─ Delega a FichaPerfilQueryOutputPort.consultarTodas(criteria)

4. FichaPerfilQueryOutputAdapter.consultarTodas(criteria)  — pura delegación
   └─ PageableMapper.toPageable(criteria, FichaPerfilSortMapper::traducir)  →  Pageable
   └─ specification.desdeCriteria(criteria)  →  Specification<FichaPerfilJpaQueryEntity>
   └─ fichaPerfilRepository.findAll(spec, pageable)  →  Page<FichaPerfilJpaQueryEntity>
   └─ .map(FichaPerfilQueryMapper::toReadModel)
   └─ PaginationMapper.toResult(page)  →  PaginatedResult<FichaPerfilReadModel>

5. FichaPerfilJpaSpecification → QueryJpaSpecification.desdeCriteria(criteria)
   └─ especDesdeNodo(raiz)
      ├─ Predicado → camposPermitidos().get("tituloProyecto").construirSpec(CONTIENE, "web")
      │              → CampoSpec.Texto → cb.like(lower(path), "%web%")
      └─ Grupo(OR) → acc.or(spec1).or(spec2)  →  (pred1 OR pred2)

6. Hibernate genera SQL:
   SELECT * FROM ficha_perfil fp JOIN asesor_ficha af ON ...
   WHERE LOWER(fp.titulo_proyecto) LIKE '%web%'
     AND (LOWER(af.nombre) LIKE '%juan%' OR LOWER(af.email) LIKE '%juan%')
   ORDER BY fp.titulo_proyecto ASC
   LIMIT 10 OFFSET 0

7. PaginatedResult<FichaPerfilReadModel> vuelve al Controller
   └─ .map(FichaPerfilResponseMapper::toResponse) → PaginatedResult<FichaPerfilResponseDTO>
   └─ Envuelto en PageResponseDTO.from(...) y retornado al cliente
```

---

## 7. Árbol de filtros — modelo de datos

### Predicado simple

```json
{
  "tipo": "PREDICADO",
  "campo": "tituloProyecto",
  "operador": "CONTIENE",
  "valor": "inteligencia"
}
```

### Grupo AND

```json
{
  "tipo": "GRUPO",
  "conector": "AND",
  "nodos": [
    { "tipo": "PREDICADO", "campo": "tituloProyecto", "operador": "CONTIENE",    "valor": "web" },
    { "tipo": "PREDICADO", "campo": "asesorEmail",    "operador": "TERMINA_CON", "valor": "@soyuco.edu.co" }
  ]
}
```

### Grupos anidados — `(A OR B) AND C`

```json
{
  "tipo": "GRUPO",
  "conector": "AND",
  "nodos": [
    {
      "tipo": "GRUPO",
      "conector": "OR",
      "nodos": [
        { "tipo": "PREDICADO", "campo": "tituloProyecto", "operador": "CONTIENE", "valor": "web" },
        { "tipo": "PREDICADO", "campo": "asesorNombre",   "operador": "CONTIENE", "valor": "juan" }
      ]
    },
    { "tipo": "PREDICADO", "campo": "asesorId", "operador": "NO_ES_NULO" }
  ]
}
```

SQL generado: `WHERE (LOWER(titulo) LIKE '%web%' OR LOWER(nombre) LIKE '%juan%') AND asesor_ficha_id IS NOT NULL`

---

## 8. Operadores por tipo de dato

| Operador | Texto | UUID | Entero | Decimal | Fecha | FechaHora | Booleano |
|----------|:-----:|:----:|:------:|:-------:|:-----:|:---------:|:--------:|
| `CONTIENE` | ✓ | — | — | — | — | — | — |
| `NO_CONTIENE` | ✓ | — | — | — | — | — | — |
| `EMPIEZA_CON` | ✓ | — | — | — | — | — | — |
| `TERMINA_CON` | ✓ | — | — | — | — | — | — |
| `ES` | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| `NO_ES` | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| `MAYOR_QUE` | — | — | ✓ | ✓ | ✓ | ✓ | — |
| `MENOR_QUE` | — | — | ✓ | ✓ | ✓ | ✓ | — |
| `MAYOR_IGUAL_QUE` | — | — | ✓ | ✓ | ✓ | ✓ | — |
| `MENOR_IGUAL_QUE` | — | — | ✓ | ✓ | ✓ | ✓ | — |
| `ES_NULO` | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| `NO_ES_NULO` | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |

Formato de valores:
- **Fecha:** `yyyy-MM-dd` (e.g. `"2024-03-15"`)
- **FechaHora:** `yyyy-MM-dd'T'HH:mm:ss` (e.g. `"2024-03-15T10:30:00"`)
- **Booleano:** `"true"` o `"false"`
- **UUID:** formato estándar con guiones (e.g. `"550e8400-e29b-41d4-a716-446655440000"`)

---

## 9. Principios SOLID aplicados

| Principio | Aplicación |
|-----------|-----------|
| **SRP** | `CampoSpec.Texto` solo maneja predicados de texto. `QueryJpaSpecification` solo recorre el árbol. `FichaPerfilJpaSpecification` solo declara qué campos son filtrables. `FichaPerfilSortMapper` solo traduce claves de sort a rutas JPA. Cada clase tiene una única razón para cambiar. |
| **OCP** | Agregar un nuevo contexto → nueva subclase de `QueryJpaSpecification`, sin modificar nada existente. Agregar un nuevo tipo de dato → nuevo record en `CampoSpec`. Agregar un campo al enum `Campo` → el switch exhaustivo señala los puntos a actualizar. |
| **LSP** | `FichaPerfilCriteria` sustituye a `QueryCriteria` en cualquier punto. Cualquier `CampoSpec.*` es intercambiable donde se espera `CampoSpec<E>`. |
| **ISP** | `CampoSpec<E>` tiene un único método (`construirSpec`). `NodoFiltroDTO` tiene un único método (`toDomain`). Ninguna clase implementa métodos que no necesita. |
| **DIP** | `QueryJpaSpecification` depende de `CampoSpec<E>` (interfaz), no de ningún record concreto. `FichaPerfilQueryOutputAdapter` depende de `QueryJpaSpecification` (abstracta), no de `FichaPerfilJpaSpecification` directamente. Las capas superiores dependen de abstracciones. |

---

## 10. Cómo extender a un nuevo contexto

Para añadir filtros dinámicos al contexto `proyectos`, por ejemplo:

### 1. Crear el criteria en `proyectos:application`

Declarar el enum `Campo` con todos los campos del contexto y sus flags. El builder
sobreescribe los dos hooks para activar las validaciones del `BaseBuilder`.

```java
// proyectos/application/.../criteria/ProyectoCriteria.java
public final class ProyectoCriteria extends QueryCriteria {

    public enum Campo {
        TITULO      ("titulo",      true,  true),
        ESTADO      ("estado",      true,  true),
        ANIO        ("anio",        true,  true),
        FECHA_INICIO("fechaInicio", true,  false),  // filtrable, NO ordenable
        ACTIVO      ("activo",      true,  false);

        private final String  clave;
        private final boolean filtrable;
        private final boolean ordenable;

        Campo(String clave, boolean filtrable, boolean ordenable) {
            this.clave     = clave;
            this.filtrable = filtrable;
            this.ordenable = ordenable;
        }

        public String getClave() { return clave; }

        static final Set<String> CLAVES_FILTRABLES = Arrays.stream(values())
                .filter(c -> c.filtrable).map(Campo::getClave)
                .collect(Collectors.toUnmodifiableSet());

        static final Set<String> CLAVES_ORDENABLES = Arrays.stream(values())
                .filter(c -> c.ordenable).map(Campo::getClave)
                .collect(Collectors.toUnmodifiableSet());
    }

    private ProyectoCriteria(Builder b) { super(b); }

    public static Builder builder() { return new Builder(); }

    public static final class Builder extends QueryCriteria.BaseBuilder<Builder> {
        @Override protected Set<String> camposFiltrables() { return Campo.CLAVES_FILTRABLES; }
        @Override protected Set<String> camposOrdenables() { return Campo.CLAVES_ORDENABLES; }
        public ProyectoCriteria build() { return new ProyectoCriteria(this); }
    }
}
```

### 2. Agregar `shared:jpa` a las dependencias de `proyectos:infrastructure`

```gradle
// proyectos/infrastructure/build.gradle
implementation project(':shared:jpa')
```

### 3. Crear la entidad de lectura y la specification concreta en `proyectos:infrastructure`

Una `{Feature}JpaQueryEntity` propia (`@Subselect`/`@Immutable`/`@Synchronize`) — **nunca**
la `ProyectoEntity` del lado `command` como tipo genérico: eso ataría el lado de lectura al
mapeo Hibernate del lado de escritura, justo lo que CQRS existe para evitar (ver CLAUDE.md
→ "CQRS isolation is absolute at the infrastructure/JPA level"). Luego, switch exhaustivo
sobre `ProyectoCriteria.Campo` para que el compilador detecte campos olvidados.

```java
// proyectos/infrastructure/.../secondaryadapter/repository/ProyectoJpaQueryEntity.java
@Entity
@Immutable
@Subselect("""
        SELECT p.id, p.titulo, p.estado, p.anio, p.fecha_inicio, p.activo
        FROM proyecto p
        """)
@Synchronize("proyecto")
@Getter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProyectoJpaQueryEntity {
    @Id @Column(name = "id") private UUID id;
    @Column(name = "titulo") private String titulo;
    @Column(name = "estado") private String estado;
    @Column(name = "anio") private Long anio;
    @Column(name = "fecha_inicio") private LocalDate fechaInicio;
    @Column(name = "activo") private Boolean activo;
}
```

```java
// proyectos/infrastructure/.../secondaryadapter/repository/ProyectoJpaSpecification.java
@Component
class ProyectoJpaSpecification extends QueryJpaSpecification<ProyectoJpaQueryEntity> {

    private static final Map<String, CampoSpec<ProyectoJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<ProyectoJpaQueryEntity>> m = new LinkedHashMap<>();
        for (ProyectoCriteria.Campo campo : ProyectoCriteria.Campo.values()) {
            CampoSpec<ProyectoJpaQueryEntity> spec = switch (campo) {
                case TITULO       -> CampoSpec.texto(root -> root.get("titulo"));
                case ESTADO       -> CampoSpec.texto(root -> root.get("estado"));
                case ANIO         -> CampoSpec.entero(root -> root.get("anio"));
                case FECHA_INICIO -> CampoSpec.fecha(root -> root.get("fechaInicio"));
                case ACTIVO       -> CampoSpec.booleano(root -> root.get("activo"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<ProyectoJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
```

### 4. Crear el SortMapper en `proyectos:infrastructure`

```java
// proyectos/infrastructure/.../ProyectoSortMapper.java
final class ProyectoSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        for (ProyectoCriteria.Campo campo : ProyectoCriteria.Campo.values()) {
            String ruta = switch (campo) {
                case TITULO       -> "titulo";
                case ESTADO       -> "estado";
                case ANIO         -> "anio";
                case FECHA_INICIO -> null;  // no ordenable
                case ACTIVO       -> null;  // no ordenable
            };
            if (ruta != null) m.put(campo.getClave(), ruta);
        }
        RUTAS = Collections.unmodifiableMap(m);
    }

    private ProyectoSortMapper() {}

    static String traducir(String clave) { return RUTAS.get(clave); }
}
```

### 5. Inyectar en el OutputAdapter — pura delegación a `shared:jpa`

Sin construir `Pageable` a mano ni atrapar excepciones de Spring Data JPA: `PageableMapper`
y `PaginationMapper` (`shared:jpa`) hacen ese trabajo.

```java
@Component
@RequiredArgsConstructor
public class ProyectoQueryOutputAdapter implements ProyectoQueryOutputPort {

    private final ProyectoQueryRepository repository;
    private final ProyectoJpaSpecification specification;

    @Override
    public PaginatedResult<ProyectoReadModel> consultarTodos(ProyectoCriteria criteria) {
        Pageable pageable = PageableMapper.toPageable(criteria, ProyectoSortMapper::traducir);
        Specification<ProyectoJpaQueryEntity> spec = specification.desdeCriteria(criteria);
        return PaginationMapper.toResult(
                repository.findAll(spec, pageable).map(ProyectoQueryMapper::toReadModel));
    }
}
```

`ProyectoQueryRepository` extiende `SpecificationQueryRepository<ProyectoJpaQueryEntity, UUID>`
(`shared:jpa`) — nunca `JpaRepository`, para que el compilador impida `save`/`delete` en el
lado de consulta. `ProyectoQueryMapper` (`final`, constructor privado, `toReadModel` estático,
sibling `mapper/`) convierte `ProyectoJpaQueryEntity → ProyectoReadModel`.

### 6. Exponer el endpoint — Controller + RequestMapper

El Controller no arma el `Criteria` inline; delega a un `{Action}RequestMapper` dedicado,
igual que el lado comando.

```java
// proyectos/infrastructure/.../primaryadapter/web/ConsultarProyectosController.java
@PostMapping("/coordinador")
public ResponseEntity<PageResponseDTO<ProyectoResponseDTO>> consultar(
        @RequestBody(required = false) QueryCriteriaRequestDTO request) {
    var resultado = consultarProyectosUseCase.ejecutar(
            ConsultarProyectosRequestMapper.toCriteria(request));
    return ResponseEntity.ok(PageResponseDTO.from(
            resultado.map(ProyectoResponseMapper::toResponse)));
}
```

```java
// proyectos/infrastructure/.../primaryadapter/web/mapper/ConsultarProyectosRequestMapper.java
public final class ConsultarProyectosRequestMapper {

    private ConsultarProyectosRequestMapper() {}

    public static ProyectoCriteria toCriteria(QueryCriteriaRequestDTO dto) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);  // body nulo → criteria vacío
        return ProyectoCriteria.builder()
                .pagina(solicitud.getPagina())
                .tamanio(solicitud.getTamanio())
                .ordenamiento(solicitud.parsearOrdenamiento())
                .raiz(solicitud.parsearFiltros())
                .build();
    }
}
```

Con estos 6 pasos, el nuevo contexto soporta la misma capacidad de filtrado dinámico
que `fichas`, sin haber modificado ninguna clase de los módulos `shared`. El enum
`Campo` actúa como fuente de verdad única: los switches exhaustivos en la spec y el
sort mapper garantizan en tiempo de compilación que ningún campo quede sin cobertura.
