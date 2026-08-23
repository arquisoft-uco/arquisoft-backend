# Guía de Contribución — Arquisoft Backend

## Antes de Contribuir

1. Lee los estándares de código: [`.claude/skills/arquisoft-estandares/SKILL.md`](.claude/skills/arquisoft-estandares/SKILL.md) (local, siempre actualizado) — el repo privado [arquisoft-docs](https://github.com/arquisoft-uco/arquisoft-docs/blob/main/docs/architecture/coding-standards.md) tiene una versión complementaria de más alto nivel
2. Asegúrate de tener asignada la tarea correspondiente

## Flujo de Trabajo

1. Crea una rama desde `develop` siguiendo la convención: `<prefijo>/<id>-<descripcion_snake_case>`
   - Ejemplo: `feature/HT-005-scaffolding_spring_boot`
2. Implementa siguiendo arquitectura hexagonal por bounded context
3. Ejecuta tests: `./gradlew test`
4. Verifica cobertura: `./gradlew jacocoTestReport` (mínimo 75%)
5. Crea un Pull Request hacia `develop` usando el template provisto
6. Espera al menos 1 review aprobado antes de mergear

## Convenciones

- **Commits:** Conventional Commits en español — `feat(proyectos): descripción`
- **Branching:** GitFlow simplificado
  - Prefijos válidos: `feature/`, `fix/`, `refactor/`, `hotfix/`, `docs/`, `test/`, `chore/`, `spike/`
  - Formato: `<prefijo>/<id>-<descripcion_snake_case>`
- **Nomenclatura:** Español para negocio (`ProyectoGrado`), inglés para sufijos técnicos (`UseCase`, `Adapter`, `DTO`)
- **Arquitectura:** Hexagonal (puertos y adaptadores) por Bounded Context
- **Testing:** JUnit 5 + Mockito + AssertJ — Convención: `debeHacerAlgo_cuandoCondicion()`

## Estructura del PR

Usa el template de PR incluido en `.github/PULL_REQUEST_TEMPLATE.md`.
