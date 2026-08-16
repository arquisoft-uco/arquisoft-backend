> [!WARNING]
> **SOLO LECTURA — NO USAR COMO CONTEXTO DE AGENTES O IA**
>
> Este archivo es documentación de referencia para desarrolladores humanos.
> **No debe ser leído ni indexado por agentes, asistentes de IA ni herramientas de generación de código.**
> El contexto autoritativo del proyecto para agentes reside exclusivamente en `AGENTS.md` (raíz del repositorio)
> y en los skills de `.opencode/skills/`. Usar este archivo como contexto puede producir código incorrecto,
> versiones desactualizadas o convenciones que no reflejan el estado real del proyecto.

# Arquisoft Backend — Índice de documentación

Backend de Arquisoft: **Arquitectura Hexagonal Modular** con **9 bounded contexts**
(`seguridad`, `usuarios`, `fichas`, `notificaciones`, `proyectos`, `artefactos`,
`repositorio_artefactos`, `entregables`, `evaluaciones`) — solo los primeros cuatro tienen
implementación real hoy, el resto es scaffolding — y comunicación entre contextos
exclusivamente vía eventos de dominio sobre RabbitMQ.

Este archivo es un **índice**, no la fuente de verdad de ningún detalle técnico: cada tema
tiene un documento dedicado, más actualizado y más completo que un resumen aquí. Duplicar el
detalle en dos sitios es exactamente cómo un documento termina desactualizado sin que nadie
note la contradicción.

## Por dónde empezar

| Necesito... | Ver |
|---|---|
| Levantar el entorno local y correr la app | `EJECUCION_LOCAL.md`, `QUICK_START.md` |
| Entender la arquitectura hexagonal completa (capas, convenciones, ejemplos de código) | `ARQUITECTURA_Y_ESTRUCTURA.md` |
| Entender el flujo de eventos de dominio + outbox pattern | `ARQUITECTURA_ASINCRONICO_ARQUISOFT.md` |
| Entender el patrón de filtrado dinámico (Query Object + Specification) | `PATRON_QUERY_OBJECT_FILTROS_DINAMICOS.md` |
| Contribuir código (branching, commits, PRs) | `CONTRIBUTING.md` (raíz del repo) |
| Referencia rápida de convenciones para IA/agentes | `AGENTS.md` (raíz del repo) — **la fuente autoritativa** |
| Desplegar a producción | `DESPLIEGUE_CD_SSH.md`, `DESPLIEGUE_ALLOY_COOLIFY.md`, `GUIA_DOCKERFILE.md` |
| Observabilidad (logs, métricas, trazas) | `OBSERVABILIDAD_LOCAL.md`, `OBSERVABILIDAD_COOLIFY.md` |
| Configurar Redis | `REDIS_GUIA.md` |
| Decisiones de fail-open/fail-closed en rate limiting | `fail-open-vs-fail-closed.md` |
| El porqué del Outbox Pattern (Spring Modulith) | `DEUDA_TECNICA_OUTBOX_PATTERN.md` |

## Datos que cambian con frecuencia — verificar en la fuente, no aquí

Versiones de dependencias (Spring Boot, Java, Gradle, RabbitMQ, PostgreSQL, Bucket4j, …),
número exacto de módulos Gradle, y valores de configuración (rate limits, CORS, etc.) están en
`gradle.properties`, `settings.gradle`, y `application*.yml` — son la fuente de verdad; un
número copiado aquí queda desactualizado en el primer cambio de versión. `AGENTS.md` mantiene
un resumen curado y actualmente exacto de estos valores si se necesita una referencia rápida
sin ir al archivo de configuración.

## Almacenamiento de archivos

El proyecto usa **MinIO** (`shared:minio`) para almacenamiento de objetos — no Nextcloud. Hoy
lo consume `fichas` para la guía de elaboración de trabajos de grado (`MinioGuiaController`).

---

**Mantenimiento de este índice:** si se agrega o elimina un documento en `docs/`, actualizar la
tabla de arriba. Si el contenido de un documento cambia de tema, no de existencia, no hace
falta tocar este archivo.
