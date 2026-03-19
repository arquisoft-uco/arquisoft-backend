# AGENTS.md — arquisoft-backend

Guía técnica para agentes de IA que trabajan en el backend modular de Arquisoft.

---

## 1) Stack y módulos reales

### Stack tecnológico

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 21 | Lenguaje principal |
| Spring Boot | 3.2.4 | Framework base |
| Spring Data JPA | Spring Boot BOM | Persistencia PostgreSQL |
| Spring Security + OAuth2 Resource Server | Spring Boot BOM | Seguridad y JWT |
| RabbitMQ (Spring AMQP) | Spring Boot BOM | Integración asíncrona |
| Flyway | 10.10.0 | Migraciones BD |
| Gradle | Wrapper del proyecto | Build multi-módulo |
| Docker | Multi-stage build | Empaquetado y despliegue |

### Build tool y comandos base

- Windows: `gradlew.bat clean build`
- Linux/macOS: `./gradlew clean build`
- Compilación rápida sin tests: `gradlew.bat compileJava`

### Módulos del monolito modular

- `shared` + submódulos: `domain`, `notifications`, `redis`, `amqp`, `web`, `postgres`, `validation`, `exceptions`
- Contextos implementados: `seguridad`, `fichas`, `proyectos`, `artefactos`, `repositorio_artefactos`, `entregables`, `evaluaciones`
- Cada contexto usa submódulos: `domain`, `application`, `infrastructure`

### Paquete raíz

`com.arquisoft`

---

## 2) Arquitectura objetivo (Hexagonal + Modular)

### Estructura por bounded context

```text
<contexto>/
├── domain/
│   └── src/main/java/com/arquisoft/<contexto>/domain/
│       ├── model/
│       ├── port/
│       │   ├── in/
│       │   └── out/
│       └── exception/
├── application/
│   └── src/main/java/com/arquisoft/<contexto>/application/
│       ├── usecase/
│       ├── dto/
│       └── facade/
└── infrastructure/
    └── src/main/java/com/arquisoft/<contexto>/infrastructure/
        ├── adapter/
        │   ├── in/
        │   └── out/
        └── config/
```

### Flujo de dependencias permitido

`Adapter In -> Port In -> UseCase -> Port Out -> Adapter Out`

Reglas:
- El dominio no depende de Spring ni de infraestructura.
- `application` depende de `domain`.
- `infrastructure` depende de `application` y `domain`.
- No acoplar contextos de negocio entre sí por clases concretas; integrar por eventos o puertos.

---

## 3) Convenciones obligatorias

### Idioma y nombrado

- Negocio en español: `ProyectoGrado`, `FichaEstudiante`, `crearProyecto`.
- Sufijos técnicos en inglés: `UseCase`, `Port`, `Adapter`, `Controller`, `DTO`, `Facade`, `Impl`.
- DTOs y entidades de negocio con nombres explícitos, sin abreviaturas ambiguas.

### Reglas de código

- Inyección por constructor (preferida). Evitar `@Autowired` en campos.
- Sin imports wildcard.
- Sin queries SQL inline en clases Java; ubicarlas en migraciones/repositorio/adaptadores apropiados.
- Sin comentarios de ruido; código autodocumentado.

### Manejo de errores

- Excepciones de dominio en capa `domain/exception`.
- Traducción HTTP en adaptadores de entrada (`Controller` / `ExceptionHandler`).
- No exponer trazas internas en respuestas REST.

### Configuración

- Configuración Spring en `src/main/resources/application*.yml`.
- Propiedades por entorno: `application.yml`, `application-dev.yml`, `application-prod.yml`.
- No introducir secretos en repositorio.

---

## 4) Integración con historias de arquisoft-docs

### Fuente de verdad funcional

- Historias técnicas: `../arquisoft-docs/docs/stories/HT-*.story.md`
- Historias priorizadas: `../arquisoft-docs/docs/stories/historias_usuario_priorizadas.md`
- Arquitectura y estándares: `../arquisoft-docs/docs/architecture/`

### Regla operativa para agentes

Antes de implementar cualquier cambio:
1. Identificar ID de historia (`HT-xxx` o `HUxxx`).
2. Leer la historia y extraer Actor, Comando, Criterios Given/When/Then.
3. Mapear cada criterio a artefactos técnicos (capas, módulos, configuración, tests).
4. Validar restricciones de arquitectura y estándares de código.

---

## 5) Checklist — modificar funcionalidad existente

1. Leer historia en `arquisoft-docs` y su contexto arquitectónico.
2. Identificar bounded context y capa(s) afectadas.
3. Revisar contratos existentes (puertos, DTO, endpoints, eventos).
4. Aplicar cambio en orden: dominio -> aplicación -> infraestructura.
5. Ajustar configuración o migraciones solo si el criterio lo exige.
6. Añadir/ajustar tests alineados a Given/When/Then.
7. Ejecutar build y tests del módulo impactado.
8. Documentar decisiones técnicas en el skill o artefacto correspondiente.

---

## 6) Checklist — crear funcionalidad nueva

1. Crear modelos y puertos en `domain`.
2. Implementar caso de uso y DTOs en `application`.
3. Implementar adaptadores `in/out` en `infrastructure`.
4. Registrar wiring/configuración mínima necesaria.
5. Incorporar publicación/consumo de eventos si aplica.
6. Crear tests unitarios y de integración según riesgo.
7. Verificar aceptación de la historia (escenarios Given/When/Then).

---

## 7) Archivos protegidos (confirmación explícita)

Solicitar confirmación del usuario antes de modificar:
- `build.gradle`
- `settings.gradle`
- `gradle.properties`
- `Dockerfile`
- `docker-compose.yml`
- `src/main/resources/application*.yml` (cuando implique cambios globales)
- `seguridad/infrastructure/.../SecurityConfig.java`

---

## 8) Testing mínimo requerido

- Framework base: JUnit 5 + Mockito (+ AssertJ opcional).
- Cobertura objetivo del cambio: mínimo 75% en código nuevo/modificado.
- Escenarios mínimos por historia:
  - flujo exitoso
  - validaciones de entrada
  - caso vacío/no encontrado
  - error de negocio esperado

---

## 9) Flujo sugerido de agentes

1. `planner`: analiza historia y genera plan técnico.
2. `implementador`: ejecuta cambios de código según plan.
3. `validator`: valida cumplimiento de plan + convenciones.
4. `logger` (opcional): estandariza trazabilidad y logs de la historia.

Este flujo aplica para cada historia técnica en `arquisoft-docs/docs/stories`.
