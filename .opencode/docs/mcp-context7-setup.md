# Guía de Configuración — Context7 MCP en OpenCode

Context7 inyecta documentación actualizada y específica por versión de cualquier librería
directamente en el contexto del agente. Es especialmente útil para consultar docs de
Spring Boot 3.2, Java 21, JPA, RabbitMQ, Flyway, Keycloak y cualquier dependencia del stack.

---

## ¿Qué hace Context7?

Cuando el agente necesita saber cómo usar una librería, en lugar de depender de su
conocimiento de entrenamiento (que puede estar desactualizado), Context7 busca la
documentación oficial y real de esa versión exacta y la entrega en el prompt.

Ejemplo de uso en un prompt:

```
¿Cómo configuro un listener de RabbitMQ con Spring AMQP 3.1? use context7
```

Context7 resuelve el ID de la librería, busca la documentación de esa versión específica
y la inyecta automáticamente en el contexto del agente.

---

## Prerequisitos

- Node.js 18 o superior instalado
- OpenCode instalado y funcionando
- (Opcional) Cuenta gratuita en [context7.com/dashboard](https://context7.com/dashboard) para mayor rate limit

---

## Paso 1 — Obtener API Key (recomendado)

Aunque Context7 funciona sin API key, tenerla aumenta el rate limit considerablemente.

1. Ve a [context7.com/dashboard](https://context7.com/dashboard)
2. Inicia sesión con GitHub
3. Copia tu API key desde el dashboard

> Sin API key funciona, pero con límite de requests más bajo. Para uso intensivo en
> desarrollo, se recomienda obtenerla.

---

## Paso 2 — Configurar Context7 en opencode.json

Tienes dos opciones: **remota** (recomendada) o **local**.

### Opción A — Remota (recomendada, sin instalar nada)

Agrega esto en tu `opencode.json` (proyecto o global `~/.config/opencode/opencode.json`):

```json
{
  "mcp": {
    "context7": {
      "type": "remote",
      "url": "https://mcp.context7.com/mcp",
      "headers": {
        "CONTEXT7_API_KEY": "TU_API_KEY"
      },
      "enabled": true
    }
  }
}
```

> Si no tienes API key, omite el bloque `"headers"` — funciona igual con rate limit básico.

### Opción B — Local (vía npx, sin depender de red para el servidor)

```json
{
  "mcp": {
    "context7": {
      "type": "local",
      "command": ["npx", "-y", "@upstash/context7-mcp", "--api-key", "TU_API_KEY"],
      "enabled": true
    }
  }
}
```

> La opción local descarga el servidor MCP con npx al iniciar opencode.
> Requiere Node.js y conexión a internet la primera vez.

---

## Paso 3 — Verificar que Context7 está activo

Inicia opencode y ejecuta en el chat:

```
/mcp
```

Deberías ver `context7` listado como servidor activo con sus herramientas disponibles:
- `resolve-library-id`
- `query-docs`

---

## Paso 4 — Agregar regla automática en AGENTS.md

Para que los agentes usen Context7 automáticamente sin que tengas que escribir
`use context7` en cada prompt, agrega esta sección a tu `AGENTS.md`:

```markdown
## Context7 MCP

Cuando necesites documentación de librerías, configuración de dependencias o ejemplos
de código de cualquier framework del stack, usa automáticamente las herramientas de
Context7 sin que el usuario te lo pida explícitamente:

- `resolve-library-id` para obtener el ID de la librería
- `query-docs` para obtener la documentación específica de esa versión

Librerías prioritarias para este proyecto:
- Spring Boot → `/spring-projects/spring-boot` (versión 3.2.4)
- Spring AMQP (RabbitMQ) → `/spring-projects/spring-amqp`
- Spring Data JPA → `/spring-projects/spring-data-jpa`
- Spring Security → `/spring-projects/spring-security`
- Flyway → `/flyway/flyway`
- Keycloak Admin Client → `/keycloak/keycloak`
- Gradle → `/gradle/gradle`
- JUnit 5 → `/junit-team/junit5`
- Mockito → `/mockito/mockito`
- AssertJ → `/assertj/assertj-core`
```

---

## Paso 5 — Uso con IDs de librería directos (avanzado)

Si ya conoces el ID de la librería en Context7, puedes saltarte el paso de resolución:

```
¿Cómo configuro @RabbitListener con acknowledge manual? use library /spring-projects/spring-amqp
```

```
¿Cómo creo una migración condicional en Flyway? use library /flyway/flyway
```

```
¿Cómo uso @DynamicPropertySource en tests de integración? use library /spring-projects/spring-framework
```

---

## IDs de Librerías del Stack Arquisoft

Guarda estos IDs para usarlos directamente en tus prompts:

| Librería | Context7 ID | Versión en proyecto |
|----------|-------------|---------------------|
| Spring Boot | `/spring-projects/spring-boot` | 3.2.4 |
| Spring AMQP (RabbitMQ) | `/spring-projects/spring-amqp` | 3.1.x |
| Spring Data JPA | `/spring-projects/spring-data-jpa` | 3.2.x |
| Spring Security | `/spring-projects/spring-security` | 6.2.x |
| Spring Web (REST) | `/spring-projects/spring-framework` | 6.1.x |
| Flyway | `/flyway/flyway` | 9.x |
| JUnit 5 | `/junit-team/junit5` | 5.10.x |
| Mockito | `/mockito/mockito` | 5.x |
| AssertJ | `/assertj/assertj-core` | 3.x |
| Lombok | `/projectlombok/lombok` | 1.18.x |
| Gradle | `/gradle/gradle` | 8.6 |

---

## Solución de Problemas

| Problema | Causa | Solución |
|----------|-------|----------|
| Context7 no aparece en `/mcp` | opencode.json mal formado | Verificar JSON válido con `cat opencode.json \| jq .` |
| `HTTP 429 Too Many Requests` | Rate limit sin API key | Obtener API key gratis en context7.com/dashboard |
| `npx: command not found` | Node.js no instalado | Instalar Node.js 18+ y reiniciar terminal |
| Documentación desactualizada | ID de librería incorrecto | Usar `resolve-library-id` primero para encontrar el ID correcto |
| Timeout en modo local | Descarga lenta de npx | Usar opción remota (Opción A) en su lugar |

---

## Configuración Completa de opencode.json (referencia)

Ejemplo con Context7 remoto y API key como variable de entorno:

```json
{
  "$schema": "https://opencode.ai/config.json",
  "mcp": {
    "context7": {
      "type": "remote",
      "url": "https://mcp.context7.com/mcp",
      "headers": {
        "CONTEXT7_API_KEY": "${CONTEXT7_API_KEY}"
      },
      "enabled": true
    }
  }
}
```

Y en tu shell (`~/.zshrc` o `~/.bashrc`):

```bash
export CONTEXT7_API_KEY="tu_api_key_aqui"
```

Esto evita hardcodear la API key en el archivo de configuración del proyecto.
