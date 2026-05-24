# Patrón Specification — Filtros Dinámicos con Agrupación Booleana

Este documento describe la implementación del patrón Specification en el proyecto,
orientada a soportar consultas dinámicas y personalizables desde sistemas externos.

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

**Antes:** cada contexto definía sus propios campos hardcodeados en el criteria y su
propia lógica de especificación.

**Después:** un árbol de expresión booleana (`NodoFiltro`) con operadores tipados
(`CampoSpec`) que se traduce automáticamente a una `Specification` JPA.

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
 FichaPerfilCriteria       ← criteria concreto (fichas:application)
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
 FichaPerfilJpaSpecification   ← spec concreta (fichas:infrastructure)
     │  extends QueryJpaSpecification<FichaPerfilJpaEntity>
     │
     ▼
 QueryJpaSpecification<E>      ← recorre NodoFiltro (shared:postgres)
     │  camposPermitidos().get(campo).construirSpec(operador, valor)
     ▼
 CampoSpec<E>                  ← predicado JPA por tipo (shared:postgres)
     │
     ▼
 Specification<FichaPerfilJpaEntity>  → SQL WHERE generado
```

---

## 3. Módulos involucrados

| Módulo | Capa | Responsabilidad en este patrón |
|--------|------|-------------------------------|
| `shared:domain` | Dominio puro | Modelo del árbol de filtros, operadores, conector, criteria base |
| `shared:postgres` | Infraestructura compartida | Traducción del árbol a predicados JPA, validación de tipos |
| `shared:web` | Transporte HTTP compartido | Deserialización JSON del árbol de filtros y body del request |
| `{ctx}:application` | Aplicación | Criteria concreto del contexto (solo builder, hereda todo) |
| `{ctx}:infrastructure` | Infraestructura | Spec JPA concreta con el mapa de campos filtrables |

> `{ctx}` = cualquier bounded context (`fichas`, `proyectos`, `evaluaciones`, etc.)

---

## 4. Estructura de carpetas

```
shared/
├── domain/src/main/java/com/arquisoft/shared/
│   └── query/
│       ├── FiltroOperador.java        ← enum de operadores de comparación
│       ├── FiltroConector.java        ← enum AND / OR
│       ├── NodoFiltro.java            ← sealed interface: árbol booleano
│       ├── SortOrder.java             ← valor de ordenamiento (campo + dirección)
│       └── QueryCriteria.java         ← clase abstracta base para criterios
│
├── postgres/src/main/java/com/arquisoft/shared/postgres/
│   ├── query/
│   │   ├── CampoSpec.java             ← sealed interface: predicado JPA por tipo
│   │   └── QueryJpaSpecification.java ← abstract: recorre el árbol y compone specs
│   └── exception/
│       └── FiltroInvalidoException.java
│
└── web/src/main/java/com/arquisoft/shared/web/dto/
    └── query/
        ├── NodoFiltroDTO.java          ← interface Jackson con @JsonTypeInfo
        ├── PredicadoFiltroDTO.java     ← DTO para nodos hoja
        ├── GrupoFiltroDTO.java         ← DTO para nodos internos
        └── QueryCriteriaRequestDTO.java ← body completo del endpoint POST

fichas/
├── application/src/main/java/com/arquisoft/fichas/application/fichaperfil/query/
│   └── criteria/
│       └── FichaPerfilCriteria.java   ← criteria concreto (solo builder)
│
└── infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/fichaperfil/query/
    ├── adapter/in/web/
    │   └── ConsultarFichasPerfilInputAdapter.java  ← endpoint POST
    └── adapter/out/persistence/
        ├── FichaPerfilJpaSpecification.java        ← mapa de campos
        └── FichaPerfilQueryOutputAdapter.java      ← ejecuta la consulta JPA
```

---

## 5. Clases e interfaces — descripción

### 5.1 `shared:domain` — capa de dominio puro

> Java puro. Sin dependencias de Spring, JPA ni Jackson. Usable en cualquier capa.

---

#### `FiltroOperador` — enum

Conjunto cerrado de operadores de comparación soportados por el sistema.

| Grupo | Operadores |
|-------|-----------|
| Texto | `CONTIENE` `NO_CONTIENE` `EMPIEZA_CON` `TERMINA_CON` |
| Igualdad (todos los tipos) | `ES` `NO_ES` |
| Comparación (números y fechas) | `MAYOR_QUE` `MENOR_QUE` `MAYOR_IGUAL_QUE` `MENOR_IGUAL_QUE` |
| Nulidad (sin valor) | `ES_NULO` `NO_ES_NULO` |

Método clave: `requiereValor()` — indica si el operador necesita un valor o no
(los operadores de nulidad no lo requieren).

---

#### `FiltroConector` — enum

Define cómo se combinan los nodos hermanos dentro de un `Grupo`.

| Valor | Semántica SQL |
|-------|--------------|
| `AND` | Todos los nodos hijos deben cumplirse |
| `OR` | Basta con que uno de los hijos se cumpla |

---

#### `NodoFiltro` — sealed interface

Árbol de expresión booleana. Tiene exactamente dos nodos:

```
NodoFiltro
├── Predicado(campo, operador, valor)   ← nodo hoja: condición atómica
└── Grupo(conector, List<NodoFiltro>)   ← nodo interno: agrupa con AND u OR
```

**`Predicado`**

- `campo` — nombre del campo a filtrar (e.g. `"tituloProyecto"`)
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

Los contextos de negocio **extienden** esta clase definiendo únicamente su `Builder`.
Ningún campo de negocio se agrega aquí; la clase es agnóstica al dominio.

**`BaseBuilder<B>`** — builder con self-type para mantener la API fluida en subclases:
```java
FichaPerfilCriteria.builder()
    .pagina(0).tamanio(10)
    .raiz(NodoFiltro.predicado(...))
    .build()
```

---

### 5.2 `shared:postgres` — capa de infraestructura compartida

> Usa Spring Data JPA (`Specification`, `Root`, `CriteriaBuilder`). No debe importarse
> desde capas de dominio ni de aplicación.

---

#### `FiltroInvalidoException` — excepción

Extiende `ApplicationException` (HTTP 400). Se lanza cuando:

- El `campo` del predicado no existe en el mapa de campos permitidos del contexto.
- El `operador` es incompatible con el tipo de dato del campo (e.g. `CONTIENE` sobre un UUID).
- El `valor` no puede parsearse al tipo esperado (UUID malformado, fecha inválida, etc.).

---

#### `CampoSpec<E>` — sealed interface

**El corazón del sistema.** Define el contrato para construir un predicado JPA dado
un operador y un valor en texto.

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
El mapa asocia el nombre del campo (tal como llega en el JSON) con su `CampoSpec`
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

DTO para nodos hoja. Campos: `campo`, `operador` (`FiltroOperador`), `valor` (nullable).

---

#### `GrupoFiltroDTO`

DTO para nodos internos. Campos: `conector` (`FiltroConector`), `nodos` (`List<NodoFiltroDTO>`).
La lista es polimórfica: cada elemento puede ser `PREDICADO` o `GRUPO`, lo que
permite el anidamiento recursivo en la deserialización.

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

#### `FichaPerfilCriteria`

Criteria concreto para el contexto fichas. **No agrega campos propios**; su única
responsabilidad es establecer el tipo concreto y exponer su builder.

```java
public final class FichaPerfilCriteria extends QueryCriteria {
    public static Builder builder() { return new Builder(); }

    public static final class Builder extends QueryCriteria.BaseBuilder<Builder> {
        public FichaPerfilCriteria build() { return new FichaPerfilCriteria(this); }
    }
}
```

Gracias a la herencia, `FichaPerfilCriteria.builder().pagina(0).tamanio(10).raiz(...).build()`
ya tiene toda la funcionalidad sin código adicional.

---

### 5.5 `fichas:infrastructure` — adaptadores concretos

---

#### `FichaPerfilJpaSpecification` — `@Component`

Extiende `QueryJpaSpecification<FichaPerfilJpaEntity>` y declara los **campos
filtrables de la entidad ficha perfil** con su tipo de dato correspondiente.

```java
private static final Map<String, CampoSpec<FichaPerfilJpaEntity>> CAMPOS = Map.of(
    "tituloProyecto", CampoSpec.texto(root -> root.get("tituloProyecto")),
    "asesorNombre",   CampoSpec.texto(root -> root.get("asesorFicha").get("nombre")),
    "asesorEmail",    CampoSpec.texto(root -> root.get("asesorFicha").get("email")),
    "asesorId",       CampoSpec.uuid(root -> root.get("asesorFicha").get("id"))
);
```

Para **agregar un nuevo campo filtrable** basta con añadir una entrada al mapa.
No se modifica ninguna otra clase.

---

#### `FichaPerfilQueryOutputAdapter` — `@Component`

Implementa `FichaPerfilQueryOutputPort`. Recibe `FichaPerfilCriteria`, delega la
construcción de la spec a `FichaPerfilJpaSpecification`, convierte el criteria a
`Pageable` para ordenamiento/paginación, ejecuta `findAll(spec, pageable)` y
mapea el resultado a `PaginatedResult<FichaPerfilReadModel>`.

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
   └─ dto.parsearFiltros()  →  NodoFiltro (árbol)
   └─ dto.parsearOrdenamiento()  →  List<SortOrder>
   └─ FichaPerfilCriteria.builder()...build()

3. ConsultarFichasPerfilUseCase.ejecutar(criteria)
   └─ Delega a FichaPerfilQueryOutputPort.consultarTodas(criteria)

4. FichaPerfilQueryOutputAdapter.consultarTodas(criteria)
   └─ specification.desdeCriteria(criteria)  →  Specification<FichaPerfilJpaEntity>
   └─ fichaPerfilJpaRepository.findAll(spec, pageable)

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
| **SRP** | `CampoSpec.Texto` solo maneja predicados de texto. `QueryJpaSpecification` solo recorre el árbol. `FichaPerfilJpaSpecification` solo declara qué campos son filtrables. Cada clase tiene una única razón para cambiar. |
| **OCP** | Agregar un nuevo contexto → nueva subclase de `QueryJpaSpecification`, sin modificar nada existente. Agregar un nuevo tipo de dato → nuevo record en `CampoSpec`. Agregar un nuevo campo filtrable → nueva entrada en el mapa de `camposPermitidos()`. |
| **LSP** | `FichaPerfilCriteria` sustituye a `QueryCriteria` en cualquier punto. Cualquier `CampoSpec.*` es intercambiable donde se espera `CampoSpec<E>`. Cualquier subclase de `QueryJpaSpecification` puede usarse en el `OutputAdapter`. |
| **ISP** | `CampoSpec<E>` tiene un único método (`construirSpec`). `NodoFiltroDTO` tiene un único método (`toDomain`). Ninguna clase implementa métodos que no necesita. |
| **DIP** | `QueryJpaSpecification` depende de `CampoSpec<E>` (interfaz), no de `CampoSpec.Texto` ni de ningún record concreto. `FichaPerfilQueryOutputAdapter` depende de `QueryJpaSpecification` (abstracta), no de `FichaPerfilJpaSpecification` directamente. Las capas superiores dependen de abstracciones definidas en capas inferiores. |

---

## 10. Cómo extender a un nuevo contexto

Para añadir filtros dinámicos al contexto `proyectos`, por ejemplo:

### 1. Crear el criteria en `proyectos:application`

```java
// proyectos/application/.../criteria/ProyectoCriteria.java
public final class ProyectoCriteria extends QueryCriteria {

    public static Builder builder() { return new Builder(); }

    public static final class Builder extends QueryCriteria.BaseBuilder<Builder> {
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

```java
// proyectos/infrastructure/.../ProyectoJpaSpecification.java
@Component
class ProyectoJpaSpecification extends QueryJpaSpecification<ProyectoJpaEntity> {

    private static final Map<String, CampoSpec<ProyectoJpaEntity>> CAMPOS = Map.of(
        "titulo",        CampoSpec.texto(root -> root.get("titulo")),
        "estado",        CampoSpec.texto(root -> root.get("estado")),
        "anio",          CampoSpec.entero(root -> root.get("anio")),
        "fechaInicio",   CampoSpec.fecha(root -> root.get("fechaInicio")),
        "activo",        CampoSpec.booleano(root -> root.get("activo"))
    );

    @Override
    protected Map<String, CampoSpec<ProyectoJpaEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
```

### 4. Inyectar en el OutputAdapter

```java
@Component
@RequiredArgsConstructor
public class ProyectoQueryOutputAdapter implements ProyectoQueryOutputPort {

    private final ProyectoJpaRepository repository;
    private final ProyectoJpaSpecification specification;

    @Override
    public PaginatedResult<ProyectoReadModel> consultarTodos(ProyectoCriteria criteria) {
        Specification<ProyectoJpaEntity> spec = specification.desdeCriteria(criteria);
        Pageable pageable = /* convertir criteria a Pageable */;
        return PaginationMapper.toResult(repository.findAll(spec, pageable).map(ProyectoMapper::toReadModel));
    }
}
```

### 5. Exponer el endpoint en el InputAdapter

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

Con estos 5 pasos, el nuevo contexto soporta la misma capacidad de filtrado dinámico
que `fichas`, sin haber modificado ninguna clase de los módulos `shared`.
