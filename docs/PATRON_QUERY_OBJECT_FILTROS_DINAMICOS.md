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
   - [shared:domain — capa de dominio puro](#51-shareddomain--capa-de-dominio-puro)
   - [shared:postgres — capa de infraestructura compartida](#52-sharedpostgres--capa-de-infraestructura-compartida)
   - [shared:web — capa de transporte HTTP](#53-sharedweb--capa-de-transporte-http)
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
 FichaPerfilQueryOutputAdapter ← adaptador JPA (fichas:infrastructure)
     │  specification.desdeCriteria(criteria)
     ▼
 FichaPerfilJpaSpecification   ← [SPRING DATA SPECIFICATION] spec concreta (fichas:infrastructure)
     │  extends QueryJpaSpecification<FichaPerfilEntity>
     │
     ▼
 QueryJpaSpecification<E>      ← recorre NodoFiltro (shared:postgres)
     │  camposPermitidos().get(campo).construirSpec(operador, valor)
     ▼
 CampoSpec<E>                  ← predicado JPA por tipo (shared:postgres)
     │
     ▼
 Specification<FichaPerfilEntity>  → SQL WHERE generado
```

La frontera entre los dos patrones está exactamente en `FichaPerfilQueryOutputPort`:
todo lo que está por encima es **Query Object**; todo lo que está por debajo es
**Spring Data Specification**.

---

## 3. Módulos involucrados

| Módulo | Capa | Patrón | Responsabilidad |
|--------|------|--------|----------------|
| `shared:domain` | Dominio puro | Query Object | Modelo del árbol de filtros, operadores, conector, criteria base, Template Method de validación |
| `shared:postgres` | Infraestructura compartida | Spring Data Specification | Traducción del árbol a predicados JPA, validación de tipos |
| `shared:web` | Transporte HTTP compartido | — | Deserialización JSON del árbol de filtros y body del request |
| `{ctx}:application` | Aplicación | Query Object | Criteria concreto del contexto (enum de campos + builder con hooks) |
| `{ctx}:infrastructure` | Infraestructura | Spring Data Specification | Spec JPA concreta con el mapa de campos filtrables; mapper de sort |

> `{ctx}` = cualquier bounded context (`fichas`, `proyectos`, `evaluaciones`, etc.)

---

## 4. Estructura de carpetas

```
shared/
├── domain/src/main/java/com/arquisoft/shared/
│   └── query/                              ← [QUERY OBJECT]
│       ├── FiltroOperador.java             ← enum de operadores de comparación
│       ├── FiltroConector.java             ← enum AND / OR
│       ├── FiltroException.java            ← excepción de dominio para validaciones del filtro
│       ├── NodoFiltro.java                 ← sealed interface: árbol booleano
│       ├── SortOrder.java                  ← valor de ordenamiento (campo + dirección)
│       └── QueryCriteria.java              ← clase abstracta base para criterios
│
├── postgres/src/main/java/com/arquisoft/shared/postgres/
│   ├── query/                              ← [SPRING DATA SPECIFICATION]
│   │   ├── CampoSpec.java                  ← sealed interface: predicado JPA por tipo
│   │   └── QueryJpaSpecification.java      ← abstract: recorre el árbol y compone specs
│   └── exception/
│       └── FiltroInvalidoException.java    ← campo inválido o tipo incompatible en infraestructura
│
└── web/src/main/java/com/arquisoft/shared/web/dto/
    └── query/
        ├── NodoFiltroDTO.java              ← interface Jackson con @JsonTypeInfo
        ├── PredicadoFiltroDTO.java         ← DTO para nodos hoja
        ├── GrupoFiltroDTO.java             ← DTO para nodos internos
        └── QueryCriteriaRequestDTO.java    ← body completo del endpoint POST

fichas/
├── application/src/main/java/com/arquisoft/fichas/application/fichaperfil/query/
│   └── criteria/
│       └── FichaPerfilCriteria.java        ← [QUERY OBJECT] criteria concreto con enum de campos
│
└── infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/fichaperfil/query/
    ├── adapter/in/web/
    │   └── ConsultarFichasPerfilInputAdapter.java      ← endpoint POST
    └── adapter/out/persistence/
        ├── FichaPerfilJpaSpecification.java            ← [SPRING DATA SPECIFICATION] mapa de campos
        ├── FichaPerfilSortMapper.java                  ← traduce claves lógicas a rutas JPA para sort
        └── FichaPerfilQueryOutputAdapter.java          ← ejecuta la consulta JPA
```

---

## 5. Clases e interfaces — descripción

### 5.1 `shared:domain` — capa de dominio puro

> Java puro. Sin dependencias de Spring, JPA ni Jackson. Usable en cualquier capa.
> Todas las clases de este paquete pertenecen al **patrón Query Object**.

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

#### `FiltroException` — excepción de dominio

Extiende `DomainException`. Se lanza dentro de `shared:domain` cuando:

- Un campo de filtro no está en los campos permitidos del contexto.
- Un campo de ordenamiento no está en los campos ordenables del contexto.
- Se usa un operador que requiere valor pero el valor es nulo o vacío.
- El árbol de filtros supera la profundidad máxima (`MAX_PROFUNDIDAD_FILTRO = 10`).

Mantiene la pureza del dominio: nunca se usa `ApplicationException` ni ninguna
clase con dependencia de Spring en este paquete.

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

### 5.2 `shared:postgres` — capa de infraestructura compartida

> Usa Spring Data JPA (`Specification`, `Root`, `CriteriaBuilder`). No debe importarse
> desde capas de dominio ni de aplicación.
> Todas las clases de este paquete pertenecen al **patrón Spring Data Specification**.

---

#### `FiltroInvalidoException` — excepción

Extiende `ApplicationException` (HTTP 400). Se lanza cuando:

- El `campo` del predicado no existe en el mapa de campos permitidos del contexto.
- El `operador` es incompatible con el tipo de dato del campo (e.g. `CONTIENE` sobre un UUID).
- El `valor` no puede parsearse al tipo esperado (UUID malformado, fecha inválida, etc.).

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
CampoSpec.uuid(root -> root.get("asesorFicha").get("id"))
CampoSpec.fecha(root -> root.get("fechaCreacion"))
CampoSpec.entero(root -> root.get("anio"))
```

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

### 5.3 `shared:web` — capa de transporte HTTP

> Usa Jackson y Lombok. Depende de `shared:domain`. Solo relevante en la capa HTTP.

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

#### `FichaPerfilJpaSpecification` — `@Component` — [SPRING DATA SPECIFICATION]

Extiende `QueryJpaSpecification<FichaPerfilEntity>`. Construye el mapa de campos
filtrables iterando `FichaPerfilCriteria.Campo` con un switch exhaustivo: si se agrega
un campo al enum sin actualizar el switch, el compilador falla.

```java
static {
    for (FichaPerfilCriteria.Campo campo : FichaPerfilCriteria.Campo.values()) {
        CampoSpec<FichaPerfilEntity> spec = switch (campo) {
            case TITULO_PROYECTO -> CampoSpec.texto(root -> root.get("tituloProyecto"));
            case ASESOR_NOMBRE   -> CampoSpec.texto(root -> root.get("asesorFicha").get("nombre"));
            case ASESOR_EMAIL    -> CampoSpec.texto(root -> root.get("asesorFicha").get("email"));
            case ASESOR_ID       -> CampoSpec.uuid(root -> root.get("asesorFicha").get("id"));
        };
        m.put(campo.getClave(), spec);
    }
}
```

---

#### `FichaPerfilSortMapper`

Clase de utilidad (final, sin instancias) que traduce claves lógicas de ordenamiento
(`"tituloProyecto"`) a rutas de propiedad JPA (`"asesorFicha.nombre"`). También itera
`FichaPerfilCriteria.Campo` con un switch exhaustivo. Devuelve `null` para los campos
declarados como no ordenables; el `OutputAdapter` convierte ese `null` en
`OrdenamientoInvalidoException` antes de que llegue a JPA.

---

#### `FichaPerfilQueryOutputAdapter` — `@Component`

Implementa `FichaPerfilQueryOutputPort`. Recibe `FichaPerfilCriteria`, delega la
construcción de la spec a `FichaPerfilJpaSpecification`, convierte el criteria a
`Pageable` (usando `FichaPerfilSortMapper` para ordenamiento), ejecuta
`findAll(spec, pageable)` y mapea el resultado a `PaginatedResult<FichaPerfilReadModel>`.

Maneja dos excepciones de Spring Data JPA y las convierte en errores HTTP 400:
- `PropertyReferenceException` → campo de ordenamiento inválido
- `InvalidDataAccessApiUsageException` → uso incorrecto de la API JPA

---

#### `ConsultarFichasPerfilInputAdapter` — `@RestController`

Endpoint REST que recibe el body de consulta y construye el criteria.

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

2. ConsultarFichasPerfilInputAdapter
   └─ Deserializa QueryCriteriaRequestDTO via Jackson
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

4. FichaPerfilQueryOutputAdapter.consultarTodas(criteria)
   └─ specification.desdeCriteria(criteria)  →  Specification<FichaPerfilEntity>
   └─ FichaPerfilSortMapper.traducir(campo)  →  ruta JPA para Pageable
   └─ fichaPerfilRepository.findAll(spec, pageable)

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

7. Resultado mapeado a PaginatedResult<FichaPerfilReadModel>
   └─ Envuelto en PageResponseDTO y retornado al cliente
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

### 2. Agregar `shared:postgres` a las dependencias de `proyectos:infrastructure`

```gradle
// proyectos/infrastructure/build.gradle
implementation project(':shared:postgres')
```

### 3. Crear la specification concreta en `proyectos:infrastructure`

Usar switch exhaustivo sobre `ProyectoCriteria.Campo` para que el compilador detecte
campos olvidados.

```java
// proyectos/infrastructure/.../ProyectoJpaSpecification.java
@Component
class ProyectoJpaSpecification extends QueryJpaSpecification<ProyectoEntity> {

    private static final Map<String, CampoSpec<ProyectoEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<ProyectoEntity>> m = new LinkedHashMap<>();
        for (ProyectoCriteria.Campo campo : ProyectoCriteria.Campo.values()) {
            CampoSpec<ProyectoEntity> spec = switch (campo) {
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
    protected Map<String, CampoSpec<ProyectoEntity>> camposPermitidos() {
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

### 5. Inyectar en el OutputAdapter

```java
@Component
@RequiredArgsConstructor
public class ProyectoQueryOutputAdapter implements ProyectoQueryOutputPort {

    private final ProyectoRepository repository;
    private final ProyectoJpaSpecification specification;

    @Override
    public PaginatedResult<ProyectoReadModel> consultarTodos(ProyectoCriteria criteria) {
        Pageable pageable = toPageable(criteria);
        Specification<ProyectoEntity> spec = specification.desdeCriteria(criteria);
        return PaginationMapper.toResult(
                repository.findAll(spec, pageable).map(ProyectoMapper::toReadModel));
    }

    private Pageable toPageable(ProyectoCriteria criteria) {
        if (criteria.tieneOrden()) {
            List<Sort.Order> orders = criteria.getOrdenamiento().stream()
                    .map(o -> {
                        String ruta = ProyectoSortMapper.traducir(o.getCampo());
                        if (ruta == null) throw new OrdenamientoInvalidoException(o.getCampo());
                        return o.getDireccion() == SortDirection.ASC
                                ? Sort.Order.asc(ruta) : Sort.Order.desc(ruta);
                    })
                    .toList();
            return PageRequest.of(criteria.getPagina(), criteria.getTamanio(), Sort.by(orders));
        }
        return PageRequest.of(criteria.getPagina(), criteria.getTamanio());
    }
}
```

### 6. Exponer el endpoint en el InputAdapter

```java
@PostMapping("/coordinador")
public ResponseEntity<PageResponseDTO<ProyectoReadModel>> consultar(
        @RequestBody(required = false) QueryCriteriaRequestDTO request) {
    QueryCriteriaRequestDTO req = request != null ? request : new QueryCriteriaRequestDTO();
    ProyectoCriteria criteria = ProyectoCriteria.builder()
        .pagina(req.getPagina())
        .tamanio(req.getTamanio())
        .ordenamiento(req.parsearOrdenamiento())
        .raiz(req.parsearFiltros())
        .build();
    return ResponseEntity.ok(PageResponseDTO.from(port.ejecutar(criteria)));
}
```

Con estos 6 pasos, el nuevo contexto soporta la misma capacidad de filtrado dinámico
que `fichas`, sin haber modificado ninguna clase de los módulos `shared`. El enum
`Campo` actúa como fuente de verdad única: los switches exhaustivos en la spec y el
sort mapper garantizan en tiempo de compilación que ningún campo quede sin cobertura.
