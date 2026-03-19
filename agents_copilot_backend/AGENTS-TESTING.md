# AGENTS-TESTING.md — arquisoft-backend

Guía para crear y mantener tests alineados a historias de `arquisoft-docs`.

---

## 1) Stack de testing

| Tecnología | Uso |
|---|---|
| JUnit 5 (`jUnitVersion=5.10.2`) | Framework de pruebas |
| Mockito | Mocking de puertos/dependencias |
| Spring Boot Test | Pruebas de integración de adaptadores |
| AssertJ / Assertions JUnit | Validaciones |

Comando base (Windows):
- `gradlew.bat test`
- `gradlew.bat :<modulo>:test`

---

## 2) Regla principal: tests derivados de aceptación

Para cada historia (`HT/HU`):

1. Leer criterios Given/When/Then.
2. Convertir cada criterio a casos de prueba.
3. Mantener trazabilidad en nombre de test o bloque Given/When/Then.

Ejemplo de nombre recomendado:
- `debeCrearProyectoGrado_cuandoDatosValidos()`
- `debeRetornarError_cuandoActorNoAutorizado()`

---

## 3) Ubicación de tests

Espejar la estructura del módulo afectado.

```text
<contexto>/<capa>/src/test/java/com/arquisoft/<contexto>/...
```

Casos comunes:
- `domain`: pruebas de reglas de negocio puras.
- `application`: pruebas de use cases con mocks de puertos.
- `infrastructure`: pruebas de controllers/adapters/config cuando aporten valor.

---

## 4) Cobertura mínima

Objetivo para código nuevo/modificado: **>= 75%** en líneas y ramas.

Escenarios mínimos por historia:
1. Happy path.
2. Validación de entrada.
3. Caso vacío/no encontrado.
4. Error de negocio esperado.
5. Edge case relevante.

---

## 5) Patrones recomendados

### UseCase (alta prioridad)

- Mockear puertos `out`.
- Verificar reglas y orquestación.
- Verificar side effects relevantes (eventos, persistencia, etc.).

### Adapter In (controller)

- Probar contrato HTTP cuando hay lógica de mapeo/validación.
- Verificar códigos de estado y estructura de error.

### Adapter Out (repository/integración)

- Probar solo lógica no trivial y mapeos.
- Evitar depender de servicios externos reales.

---

## 6) Restricciones

- No testear getters/setters triviales.
- No usar `Thread.sleep()` salvo justificación técnica clara.
- No acoplar tests a orden de ejecución.
- No introducir nuevas dependencias de testing sin confirmación.

---

## 7) Comandos de validación sugeridos

- Módulo puntual: `gradlew.bat :seguridad:infrastructure:test`
- Build completo: `gradlew.bat clean build`

Si falla algo fuera del alcance de la historia, reportarlo como hallazgo separado.
