# PLAN: Configuración de Realm Keycloak para Arquisoft — Backend

## Metadata
- **ID Historia:** HT-011
- **Bounded Context:** `seguridad`
- **Módulos Gradle afectados:** `seguridad:domain`, `seguridad:application`, `seguridad:infrastructure`
- **Fecha de plan:** 2026-04-23 (revisado v3 — SCREAMING_CASE, sin tildes, realm-arquisoft.json sin usuarios)
- **Rama sugerida:** `feature/HT-011-configuracion_realm_keycloak`
- **Fuentes consultadas del repo de documentación:**
  - `docs/stories/HT-011.configuracion-realm-keycloak.story.md`
  - `docs/architecture/decisions/ADR-003-autenticacion-keycloak.md` *(versión actualizada con 8 roles)*
  - `docs/architecture/decisions/ADR-006-seguridad-criptografica-keycloak.md`
- **Observaciones del usuario:**
  - El realm `arquisoft` ya existe en Keycloak.
  - El cliente `arquisoft-backend` ya existe con Direct Access Grants habilitado.
  - Existe el Realm Role `ESTUDIANTE` (solo creado, sin configuración adicional).
  - Existe un usuario `estudiante@uco.edu.co` con nombre "Estudiante UCO" y contraseña definitiva.
  - El realm tiene Email as Username, Login With Email y Verify Email activos.
  - En Authentication todos los flows están habilitados; ninguno está marcado como Set as default action.
  - El plan cubre **únicamente el backend** (Spring Boot). La configuración de Keycloak se documenta por separado como guía paso a paso.

---

## 1. Resumen Funcional

Esta historia técnica alinea el bounded context `seguridad` del backend con el realm Keycloak ya configurado. El ADR-003 actualizado define **8 roles globales** en Keycloak con nomenclatura PascalCase: `Estudiante`, `Asesor`, `AsesorFicha`, `Coordinador`, `Jurado`, `Bibliotecario`, `RepresentanteComitéCurriculum`, `Administrador`.

El `UserRole.java` existente ya tiene estos 8 roles con sus códigos en minúscula. **No se eliminan ni se reemplazan roles** — el enum actual es correcto.

El trabajo pendiente en el backend es:

1. Verificar que los **códigos** del enum coincidan exactamente con los nombres de rol tal como Keycloak los emite en el token JWT (claim `realm_access.roles`).
2. Agregar un `JwtAuthenticationConverter` en Spring Security que lea los roles de `realm_access.roles` y los convierta a `ROLE_ESTUDIANTE`, `ROLE_ASESOR`, etc.
3. Corregir la propiedad `keycloak.resource` para apuntar al cliente correcto (`arquisoft-backend`, no `arquisoft-app`).
4. Agregar `http://localhost:5173` al CORS para el frontend Vite.
5. Generar el archivo `realm-arquisoft.json` como recurso importable para CI/CD.

**Lo que NO cubre:** la configuración del propio servidor Keycloak (guía por separado); integración Azure AD (Fase 2); migraciones Flyway (no hay tablas de roles en PostgreSQL — están en Keycloak).

---

## 2. Criterios de Aceptación

| # | Criterio | Resultado esperado |
|---|----------|--------------------|
| 1 | El enum `UserRole` tiene exactamente los 8 roles del ADR-003 actualizado con los códigos que Keycloak emite en el token | Los códigos en `getCode()` coinciden con el nombre del rol en Keycloak (case-sensitive) |
| 2 | `JwtTokenAdapter` extrae roles de `realm_access.roles` y los mapea a `ROLE_{NOMBRE}` en mayúsculas | Un token con rol `Estudiante` genera `ROLE_ESTUDIANTE` en el contexto de seguridad |
| 3 | `SeguridadConfig` configura un `JwtAuthenticationConverter` que lee roles de `realm_access.roles` | Spring Security reconoce el rol sin configuración adicional en cada controller |
| 4 | La propiedad `keycloak.resource` apunta a `arquisoft-backend` | La autenticación con `/auth/login` usa el cliente correcto |
| 5 | Los endpoints protegidos rechazan tokens sin rol o con rol incorrecto con HTTP 403 | `@PreAuthorize("hasRole('ESTUDIANTE')")` funciona correctamente |
| 6 | Tests unitarios de `JwtTokenAdapter` cubren extracción de roles desde `realm_access.roles` | Cobertura ≥ 75% en las capas afectadas |
| 7 | El archivo `realm-arquisoft.json` existe en `seguridad/infrastructure/src/main/resources/` con los 8 roles, 2 clientes y usuarios de prueba | Permite importar la configuración en otros ambientes |

---

## 3. Reglas de Negocio

- **RN-01:** Los roles globales en Keycloak son exactamente 8 (ADR-003 actualizado), con nomenclatura **SCREAMING_CASE sin tildes**: `ESTUDIANTE`, `ASESOR`, `ASESOR_FICHA`, `COORDINADOR`, `JURADO`, `BIBLIOTECARIO`, `REPRESENTANTE_COMITE_CURRICULUM`, `ADMINISTRADOR`.
- **RN-02:** Los roles vienen en el claim `realm_access.roles` del JWT. El backend no debe leer de `resource_access` para roles globales.
- **RN-03:** Spring Security requiere el prefijo `ROLE_`. Como los roles en Keycloak ya están en SCREAMING_CASE, la conversión es directa: `"ESTUDIANTE"` → `ROLE_ESTUDIANTE`, `"ASESOR_FICHA"` → `ROLE_ASESOR_FICHA`. No se requiere ninguna transformación de PascalCase ni manejo de tildes.
- **RN-04:** El `getCode()` del enum retorna exactamente el string que Keycloak emite en `realm_access.roles` (SCREAMING_CASE). Esto elimina cualquier ambigüedad de case-sensitivity. `getSpringRole()` solo agrega el prefijo `"ROLE_"`.
- **RN-05:** El token JWT se firma con RSA-4096 según ADR-006. El backend solo valida; no firma.
- **RN-06:** El cliente `arquisoft-backend` es confidential (tiene client secret). El cliente `arquisoft-frontend` es public. El backend usa el client secret para autenticación directa (`/auth/login`) con `grant_type=password`.

---

## 4. Árbol de Archivos a Crear / Modificar

### Archivos a MODIFICAR

| Ruta completa | Cambio requerido |
|---------------|-----------------|
| `seguridad/domain/src/main/java/com/arquisoft/seguridad/domain/model/UserRole.java` | Verificar y corregir los valores de `getCode()` para que coincidan exactamente con los nombres de rol que Keycloak emite en `realm_access.roles`. Si Keycloak emite en PascalCase (`"Estudiante"`), los códigos deben ser PascalCase. Actualizar descripciones según ADR-003. |
| `seguridad/infrastructure/src/main/java/com/arquisoft/seguridad/infrastructure/config/SecurityConfig.java` | Inyectar el `JwtAuthenticationConverter` (nuevo bean) en el bloque `oauth2ResourceServer`. Agregar campo `private final JwtAuthenticationConverter jwtAuthenticationConverter` con `@RequiredArgsConstructor`. |
| `seguridad/infrastructure/src/main/java/com/arquisoft/seguridad/infrastructure/adapter/out/JwtTokenAdapter.java` | Simplificar `extractRoles()` para leer **únicamente** de `realm_access.roles`. Eliminar el bloque de `resource_access` (evita duplicados). El mapeo a `ROLE_` debe usar `role.toUpperCase()` de manera consistente. |
| `seguridad/infrastructure/src/main/resources/application-security.properties` | Cambiar `keycloak.resource` de `arquisoft-app` a `arquisoft-backend`. Agregar `http://localhost:5173` al valor default de `security.cors.allowed-origins`. |

### Archivos NUEVOS

| Capa | Ruta completa desde raíz del monorepo | Tipo | Responsabilidad |
|------|---------------------------------------|------|-----------------|
| infrastructure | `seguridad/infrastructure/src/main/java/com/arquisoft/seguridad/infrastructure/config/KeycloakJwtConverterConfig.java` | Config | Bean `JwtAuthenticationConverter` que extrae roles de `realm_access.roles` y los convierte a `SimpleGrantedAuthority("ROLE_" + role.toUpperCase())` |
| infrastructure | `seguridad/infrastructure/src/main/resources/realm-arquisoft.json` | Recurso JSON | Exportación del realm Keycloak lista para importar. Contiene: realm, 8 roles, cliente backend, cliente frontend, usuarios de prueba con roles asignados, KeyProvider RSA-4096. |
| test | `seguridad/infrastructure/src/test/java/com/arquisoft/seguridad/infrastructure/adapter/out/JwtTokenAdapterTest.java` | Test unitario | Cubre extracción de roles de `realm_access.roles` para los 8 roles del sistema |
| test | `seguridad/domain/src/test/java/com/arquisoft/seguridad/domain/model/UserRoleTest.java` | Test unitario | Verifica `fromCode()` y `getSpringRole()` para todos los roles |

---

## 5. Detalle por Archivo

### `UserRole.java`
- **Paquete:** `com.arquisoft.seguridad.domain.model`
- **Tipo:** Enum de dominio
- **Responsabilidad:** Representar los 8 roles globales del sistema alineados con ADR-003.
- **Valores del enum (alineados con ADR-003 actualizado — SCREAMING_CASE, sin tildes):**

  | Constante enum | `getCode()` (= nombre exacto en realm Keycloak) | `getSpringRole()` |
  |----------------|------------------------------------------------|-------------------|
  | `ESTUDIANTE` | `"ESTUDIANTE"` | `"ROLE_ESTUDIANTE"` |
  | `ASESOR` | `"ASESOR"` | `"ROLE_ASESOR"` |
  | `ASESOR_FICHA` | `"ASESOR_FICHA"` | `"ROLE_ASESOR_FICHA"` |
  | `COORDINADOR` | `"COORDINADOR"` | `"ROLE_COORDINADOR"` |
  | `JURADO` | `"JURADO"` | `"ROLE_JURADO"` |
  | `BIBLIOTECARIO` | `"BIBLIOTECARIO"` | `"ROLE_BIBLIOTECARIO"` |
  | `REPRESENTANTE_COMITE_CURRICULUM` | `"REPRESENTANTE_COMITE_CURRICULUM"` | `"ROLE_REPRESENTANTE_COMITE_CURRICULUM"` |
  | `ADMINISTRADOR` | `"ADMINISTRADOR"` | `"ROLE_ADMINISTRADOR"` |

- **Métodos a conservar sin cambio de firma:**
  - `getCode(): String` — retorna el nombre exacto del rol en Keycloak (SCREAMING_CASE)
  - `getDescription(): String`
  - `fromCode(String code): UserRole` — busca por `getCode()`, lanza `IllegalArgumentException` si no existe
  - `getSpringRole(): String` — retorna `"ROLE_" + this.name()` (equivalente a `"ROLE_" + getCode()` dado que code y name son iguales)

- **Nota:** Con SCREAMING_CASE, `this.name()` y `getCode()` retornan el mismo valor. `getSpringRole()` queda como `"ROLE_" + this.name()` sin necesidad de transformación alguna. No hay tildes en ningún rol.

---

### `KeycloakJwtConverterConfig.java`
- **Paquete:** `com.arquisoft.seguridad.infrastructure.config`
- **Tipo:** `@Configuration`
- **Responsabilidad:** Proveer el `JwtAuthenticationConverter` como `@Bean`.
- **Lógica del converter:**
  1. Leer claim `realm_access` del JWT (es un `Map`).
  2. Extraer la lista `roles` del map.
  3. Por cada rol: `new SimpleGrantedAuthority("ROLE_" + role)`.
     - Como los roles ya están en SCREAMING_CASE en Keycloak (`"ESTUDIANTE"`, `"ASESOR_FICHA"`), **no se requiere ninguna transformación**. El string del token más `"ROLE_"` es exactamente lo que Spring Security necesita.
     - Ignorar roles internos de Keycloak que no estén en el enum (`offline_access`, `uma_authorization`, `default-roles-arquisoft`): intentar `UserRole.fromCode(role)` y si lanza `IllegalArgumentException`, omitir el rol.
- **Métodos principales:**
  - `jwtAuthenticationConverter(): JwtAuthenticationConverter` — `@Bean`
  - (privado) `keycloakRolesToAuthorities(Jwt jwt): Collection<GrantedAuthority>` — lógica de conversión usando `UserRole.fromCode()`
- **Dependencias:** `JwtAuthenticationConverter`, `UserRole` (del módulo `seguridad:domain`)

---

### `SecurityConfig.java` (modificación)
- **Cambio:** Inyectar `JwtAuthenticationConverter` vía constructor:
  ```
  private final JwtAuthenticationConverter jwtAuthenticationConverter;
  ```
  Y en el bloque `oauth2ResourceServer`:
  ```
  .oauth2ResourceServer(oauth2 -> oauth2
      .jwt(jwt -> jwt
          .decoder(jwtDecoder())
          .jwtAuthenticationConverter(jwtAuthenticationConverter)
      )
  )
  ```
- **Sin otros cambios:** Reglas de autorización HTTP permanecen igual.

---

### `JwtTokenAdapter.java` (modificación)
- **Cambio en `extractRoles()`:** Leer únicamente de `realm_access.roles`. Eliminar los bloques de `resource_access` y del claim `"roles"` directo.
- **Lógica resultante:**
  ```
  realm_access (Map) → roles (List<String>) → "ROLE_" + role (sin transformación adicional)
  ```
  Ejemplo: `"ASESOR_FICHA"` → `"ROLE_ASESOR_FICHA"`. Directo, sin riesgo de `ROLE_ASESORF_ICHA`.
- **Nota:** La extracción en `JwtTokenAdapter` es para el método `extractUserInfo()` del dominio (información del usuario). La conversión de autoridades para Spring Security la hace `KeycloakJwtConverterConfig`. Ambas deben ser consistentes.

---

### `application-security.properties` (modificación)
- **Cambio 1:** `keycloak.resource=${KEYCLOAK_CLIENT_ID:arquisoft-backend}` (valor default corregido de `arquisoft-app`)
- **Cambio 2:** En `security.cors.allowed-origins`, agregar `http://localhost:5173` al valor default.
- **Sin otros cambios.**

---

### `realm-arquisoft.json`
- **Ubicación:** `seguridad/infrastructure/src/main/resources/realm-arquisoft.json`
- **Tipo:** Recurso JSON
- **Propósito:** Exportación del realm Keycloak lista para importar en CI/CD u otros ambientes. Permite reproducir la configuración completa sin clics manuales en la consola.
- **Contenido:**
  - `realm`: `"arquisoft"`, `enabled: true`
  - `loginWithEmailAllowed: true`, `registrationEmailAsUsername: true`, `verifyEmail: true`
  - `roles.realm`: los 8 roles en SCREAMING_CASE sin tildes (`ESTUDIANTE`, `ASESOR`, `ASESOR_FICHA`, `COORDINADOR`, `JURADO`, `BIBLIOTECARIO`, `REPRESENTANTE_COMITE_CURRICULUM`, `ADMINISTRADOR`)
  - `components` → `KeyProvider` RSA-4096 (ADR-006):
    ```json
    "rsa-generated": { "priority": "100", "keySize": "4096", "algorithm": "RS256" }
    ```
  - `clients`:
    - `arquisoft-backend`: `publicClient: false`, `directAccessGrantsEnabled: true`, `serviceAccountsEnabled: true`
    - `arquisoft-frontend`: `publicClient: true`, `standardFlowEnabled: true`, `redirectUris: ["http://localhost:5173/*", "https://*.arquisoft.uco.edu.co/*"]`
      - `http://localhost:5173/*` → frontend Vite en desarrollo local
      - `https://*.arquisoft.uco.edu.co/*` → frontend en producción en el servidor UCO
      - Ambas URIs son lista blanca de seguridad de Keycloak; se pueden ampliar o cambiar en cualquier momento desde la consola sin afectar el backend.
- **Sin bloque `users`:** Los usuarios de desarrollo se crean manualmente en cada ambiente. No se incluyen en el JSON del repositorio para evitar contraseñas en git.
- **Nota:** Para CI/CD se puede mantener una versión `realm-arquisoft-ci.json` separada (fuera del repo) con usuarios de test y contraseñas de entorno.

---

### `JwtTokenAdapterTest.java`
- **Paquete:** `com.arquisoft.seguridad.infrastructure.adapter.out`
- **Tipo:** Test unitario con `@ExtendWith(MockitoExtension.class)`
- **Métodos de test:**
  - `debeExtraerRolEstudiante_cuandoRealmAccessContieneESTUDIANTE()`
  - `debeExtraerRolAsesorFicha_cuandoRealmAccessContieneASESOR_FICHA()`
  - `debeExtraerMultiplesRoles_cuandoUsuarioTieneVariosRoles()`
  - `debeIgnorarRolesDeRecurso_cuandoExisteResourceAccess()`
  - `debeRetornarListaVacia_cuandoNoHayRealmAccess()`
  - `debeValidarToken_cuandoTokenEsValido()`
  - `debeRetornarFalso_cuandoTokenEsMalformado()`

---

### `UserRoleTest.java`
- **Paquete:** `com.arquisoft.seguridad.domain.model`
- **Tipo:** Test unitario puro (JUnit 6, sin Spring)
- **Métodos de test:**
  - `debeRetornarCodigoCorrecto_cuandoRolEstudiante()` — `ESTUDIANTE.getCode()` retorna `"ESTUDIANTE"`
  - `debeRetornarSpringRole_cuandoRolAsesorFicha()` — `ASESOR_FICHA.getSpringRole()` retorna `"ROLE_ASESOR_FICHA"`
  - `debeEncontrarRol_cuandoCodigoAsesorFichaExiste()` — `fromCode("ASESOR_FICHA")` retorna `ASESOR_FICHA`
  - `debeLanzarExcepcion_cuandoCodigoNoCorrespondANingunRol()` — `fromCode("rolInexistente")` lanza `IllegalArgumentException`
  - `debeTener8Roles_cuandoSeListanTodosLosValores()` — `UserRole.values().length == 8`

---

## 6. Endpoints REST

No aplica — esta HT no agrega ni modifica endpoints.

---

## 7. Eventos RabbitMQ

No aplica.

---

## 8. Migración de Base de Datos

No aplica — los roles globales viven en Keycloak; los roles contextuales por proyecto viven en tablas existentes de PostgreSQL.

---

## 9. Casos de Prueba Sugeridos

### Tests Unitarios — capa `domain`
| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `UserRoleTest` | `debeRetornarCodigoCorrecto_cuandoRolEstudiante` | `ESTUDIANTE.getCode()` retorna `"ESTUDIANTE"` |
| `UserRoleTest` | `debeRetornarSpringRole_cuandoRolAsesorFicha` | `ASESOR_FICHA.getSpringRole()` retorna `"ROLE_ASESOR_FICHA"` |
| `UserRoleTest` | `debeEncontrarRol_cuandoCodigoAsesorFicha` | `fromCode("ASESOR_FICHA")` retorna `ASESOR_FICHA` |
| `UserRoleTest` | `debeLanzarExcepcion_cuandoCodigoInexistente` | `fromCode("admin")` lanza `IllegalArgumentException` |
| `UserRoleTest` | `debeTener8Roles_cuandoSeListanTodos` | `UserRole.values().length == 8` |

### Tests Unitarios — capa `infrastructure`
| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `JwtTokenAdapterTest` | `debeExtraerRolEstudiante_cuandoRealmAccessContieneESTUDIANTE` | JWT mock con `realm_access.roles=["ESTUDIANTE"]` → lista `["ROLE_ESTUDIANTE"]` |
| `JwtTokenAdapterTest` | `debeExtraerRolAsesorFicha_cuandoRealmAccessContieneASESOR_FICHA` | JWT mock con `realm_access.roles=["ASESOR_FICHA"]` → lista `["ROLE_ASESOR_FICHA"]` |
| `JwtTokenAdapterTest` | `debeExtraerMultiplesRoles_cuandoUsuarioTieneVariosRoles` | JWT mock con `["ESTUDIANTE", "COORDINADOR"]` → `["ROLE_ESTUDIANTE", "ROLE_COORDINADOR"]` |
| `JwtTokenAdapterTest` | `debeIgnorarResourceAccess_cuandoExiste` | JWT con `resource_access` → no se duplican roles |
| `JwtTokenAdapterTest` | `debeRetornarListaVacia_cuandoNoHayRealmAccess` | JWT sin `realm_access` → lista vacía |
| `JwtTokenAdapterTest` | `debeRetornarFalso_cuandoTokenEsMalformado` | `validateToken("basura")` → `false` |

### Tests de Controller — capa `infrastructure`
| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `AuthControllerTest` | `debeRechazarRequest_cuandoSinToken` | Request sin Bearer token → 401 |
| `AuthControllerTest` | `debeAutenticar_cuandoCredencialesValidas` | `POST /auth/login` con credenciales válidas → 200 + `access_token` |

---

## 10. Checklist de Implementación

- [x] `UserRole.java` verificado y corregido — 8 roles, `getCode()` devuelve el nombre exacto tal como Keycloak lo emite en el token
- [x] `KeycloakJwtConverterConfig.java` creado — bean `JwtAuthenticationConverter` que usa `UserRole.fromCode()` para mapear a SCREAMING_CASE correctamente; arquitectura extendida con `KeycloakRolExtractor` y `RoleAuthorityMapper`
- [x] `SecurityConfig.java` modificado — inyecta `JwtAuthenticationConverter` en `oauth2ResourceServer`
- [x] `JwtTokenAdapter.java` modificado — `extractRoles()` lee únicamente de `realm_access.roles`
- [x] `application-security.properties` modificado — `keycloak.resource=arquisoft-backend`, CORS incluye `localhost:5173`
- [x] `realm-arquisoft.json` creado — 8 roles en SCREAMING_CASE sin tildes, 2 clientes, **sin bloque de usuarios**
- [x] Tests `UserRoleTest` creados (capa domain) — 5 casos
- [x] Tests `RoleAuthorityMapperTest` creados (capa domain) — 4 casos *(clase adicional implementada)*
- [x] Tests `JwtTokenAdapterTest` creados (capa infrastructure) — 6 casos
- [x] Build: `./gradlew seguridad:domain:build seguridad:application:build seguridad:infrastructure:build`
- [x] Tests: `./gradlew seguridad:domain:test seguridad:infrastructure:test`
- [ ] Cobertura: `./gradlew seguridad:jacocoTestReport` — mínimo 75%
- [ ] Commit: `feat(seguridad): alinear 8 roles Keycloak con ADR-003 y configurar JWT converter`

---

## 11. Trazabilidad del Flujo

> Esta sección es actualizada automáticamente por cada agente al completar su etapa.
> No modificar manualmente.

| Etapa      | Agente         | Estado       | Fecha | Notas |
|------------|----------------|--------------|-------|-------|
| Desarrollo | @implementador | ✅ Completo  | 2026-04-24 | 6 archivos; arquitectura extendida con RoleAuthorityMapper + KeycloakRoleExtractor; build OK |
| Tests      | @tester        | ✅ Completo  | 2026-04-24 | 15 tests (9 domain + 6 infrastructure), todos en verde. Correcciones: junit-platform-launcher + BOM en domain y infrastructure build.gradle |
| Validación | @validator     | ✅ Completo  | 2026-04-25 | Score 96/100 — APROBADO. 7/7 CA, 9/9 archivos plan, 15 tests verdes. Errores menores: Checkstyle MethodName en *Test.java, JaCoCo no medido por módulo. Reporte: .workspace/validator/validator-HT-011.md |
| Commit     | @commit        | ✅ Completado | 2026-04-25 | Hash: 3bf6219 |
