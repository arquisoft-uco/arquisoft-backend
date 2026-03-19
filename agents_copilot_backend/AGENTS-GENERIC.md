# AGENTS-GENERIC.md — plantilla base Arquisoft

Plantilla reutilizable para configurar agentes en repos del ecosistema Arquisoft.

---

## 1) Contexto mínimo a completar

- Nombre del repo: `{repo}`
- Lenguaje / runtime: `{stack}`
- Build tool: `{gradle|maven|npm|otro}`
- Arquitectura: `Hexagonal/DDD` (por defecto)
- Ruta de historias: `../arquisoft-docs/docs/stories/`
- Ruta de estándares: `../arquisoft-docs/docs/architecture/coding-standards.md`

---

## 2) Contrato de trabajo de los agentes

Cada agente debe operar con este orden:

1. Leer historia (`HT-*.story.md` o HU en consolidado)
2. Extraer Actor + Objetivo + Beneficio + Given/When/Then
3. Traducir criterios a tareas técnicas verificables
4. Implementar por capas (Domain -> Application -> Infrastructure)
5. Validar con tests y checks del repositorio

---

## 3) Convenciones transversales

- Negocio en español, sufijos técnicos en inglés (`UseCase`, `Port`, `Adapter`, `Controller`, `DTO`, `Facade`).
- Inyección por constructor.
- Evitar dependencias innecesarias.
- No modificar archivos protegidos sin confirmación explícita.
- Mantener cambios pequeños, trazables y alineados al criterio de aceptación.

---

## 4) Estructura recomendada por bounded context

```text
<contexto>/
├── domain/
│   ├── model/
│   ├── port/in/
│   ├── port/out/
│   └── exception/
├── application/
│   ├── dto/
│   ├── usecase/
│   └── facade/
└── infrastructure/
    ├── adapter/in/
    ├── adapter/out/
    └── config/
```

---

## 5) Criterios de aceptación como tests

Para cada historia:

- Cada `Given/When/Then` debe mapearse al menos a un test.
- Priorizar tests de caso de uso y adaptadores de entrada.
- Evitar tests de getters/setters triviales.
- Cobertura recomendada del cambio: >= 75%.

---

## 6) Archivos protegidos (plantilla)

Definir por repositorio, por defecto:

- Build global (`build.gradle`, `settings.gradle`, `pom.xml`)
- Config global (`application*.yml`, docker files)
- Seguridad global (`SecurityConfig` o equivalente)

---

## 7) Salida esperada del agente implementador

- Plan de arquitectura por historia
- Estructura de carpetas/paquetes impactados
- Lista de archivos creados/modificados
- Tests asociados a criterios de aceptación
- Resultado de compilación/tests
