# Rate Limiting: Token Bucket, Fail-Open y Fail-Closed

---

## Parte 1 — Cómo funciona el Token Bucket (algoritmo del balde con fichas)

### La analogía

Imagina un **balde con fichas**. Cada ficha representa el permiso para procesar una petición:

```
        [  ●  ●  ●  ●  ●  ]   ← balde con 5 fichas (capacidad máxima)
           ↑ cada petición toma 1 ficha
           ↑ si no hay fichas → HTTP 429 (Too Many Requests)
           ↑ las fichas se recargan automáticamente con el tiempo
```

### Flujo completo de una petición

```
Cliente envía petición
        ↓
  ¿Hay fichas en el balde?
        ├── SÍ → consume 1 ficha → petición PASA → 200 OK
        └── NO → petición RECHAZADA → 429 Too Many Requests
```

### Ejemplo concreto: bucket general (60 req/min)

Configuración real del proyecto:
```java
Bandwidth.builder()
    .capacity(60)                                      // balde = 60 fichas
    .refillIntervally(60, Duration.ofMinutes(1))       // recarga: 60 fichas cada 60 s
    .build()
```

**Simulación:**

| Tiempo | Evento | Fichas en balde |
|---|---|---|
| 00:00 | Balde creado | 60 |
| 00:01 | Petición llega | 59 |
| 00:15 | 14 peticiones más | 45 |
| 00:45 | 45 peticiones más | 0 |
| 00:46 | Petición llega | **429 — 0 fichas** |
| 01:00 | Recarga completa | 60 (de golpe) |
| 01:01 | Petición llega | 59 |

> `refillIntervally` = **recarga todo de una vez** al final del intervalo.
> Esto es simple y eficiente, pero tiene una vulnerabilidad: el ataque de ventana fija.

---

## El ataque de ventana fija (Fixed Window Attack)

Con `refillIntervally`, hay un momento en que el balde está lleno Y se acaba de recargar.
Un atacante puede explotar la frontera entre dos ventanas:

```
           ventana 1              ventana 2
   |----------------------|----------------------|
   ...                   59:59  60:00           ...
                            ↑      ↑
                         3 req   3 req  = 6 req en ~1 segundo
                         (agota) (recarga completa)
```

Si el límite de login es **3 intentos/min** con `refillIntervally`:
- A las 11:59:58 → envía 3 intentos → los 3 pasan
- A las 12:00:00 → balde se recarga → envía 3 intentos → los 3 pasan también
- **Resultado: 6 intentos de contraseña en 2 segundos** — el límite es inútil

### Solución: refillGreedy (recarga gradual)

```java
Bandwidth.builder()
    .capacity(3)
    .refillGreedy(3, Duration.ofMinutes(1))   // recarga: 1 ficha cada 20 s
    .build()
```

Con `refillGreedy`, los tokens se reabastecen **gradualmente**:
- 3 fichas en 60 segundos = **1 ficha cada 20 segundos**

```
| Tiempo | Fichas | Evento                          |
|--------|--------|---------------------------------|
| 00:00  |   3    | Bucket creado                   |
| 00:01  |   2    | Intento de login → pasa         |
| 00:02  |   1    | Intento de login → pasa         |
| 00:03  |   0    | Intento de login → pasa         |
| 00:04  |   0    | Intento de login → 429          |
| 00:20  |   1    | +1 ficha recargada              |
| 00:21  |   0    | Intento de login → pasa         |
| 00:40  |   1    | +1 ficha recargada              |
| 00:60  |   2    | +1 ficha más (máx 3)            |
```

**Ahora el ataque de ventana fija no funciona:**
- A las 11:59:58 → envía 3 intentos → pasan (balde vacío)
- A las 12:00:00 → NO se recarga todo de golpe
- La próxima ficha llega a las 12:00:20 (20 s después del último consumo)
- **Resultado: máximo 3 intentos cada 60 segundos sin importar cuándo empiezan**

---

## Los buckets en este proyecto

> Los buckets viven en **Redis** (`RedisBucketResolver`, Bucket4j + Lettuce), no en un mapa en
> memoria de la JVM — por eso ya no existe una propiedad `max-tracked-ips`: se eliminó al migrar
> de un `ConcurrentHashMap` por IP a Redis. Cada bucket es una clave Redis
> (`arquisoft:ratelimit:global:{ip}` / `arquisoft:ratelimit:login:{ip}`) con expiración
> automática (`ExpirationAfterWriteStrategy`, 2 minutos desde el último refill) — Redis limpia
> las IPs inactivas solo, sin lógica de aplicación.

### 1. Bucket general (`RedisBucketResolver.resolveBucket(ip)`)

```java
// Para cualquier endpoint autenticado
capacity = requestsPerMinute          // 100 en dev, 60 en prod
refillIntervally(N, 1 minuto)         // recarga de golpe
```

**Visualización:**
```
[●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●]  ← 60 fichas
                              cada petición consume 1
                              cada 60s se recargan todas
```

Uso normal: un usuario que hace 1-2 req/s está muy por debajo del límite.
Uso abusivo: un scraper que hace 100 req/s agota el balde en <1 segundo y recibe 429.

---

### 2. Bucket de login (`RedisBucketResolver.resolveLoginBucket(ip)`)

```java
// Solo para POST /auth/login
capacity = loginRequestsPerMinute     // 5 en dev, 3 en prod
refillGreedy(N, 1 minuto)             // 1 ficha cada (60/N) segundos
```

**Visualización (3 req/min):**
```
[● ● ●]  ← 3 fichas
  ↓ ↓ ↓    3 intentos de contraseña → balde vacío
[ . . .]  ← 0 fichas
  20s...   +1 ficha
[●  .  .]
  20s...   +1 ficha
[● ●  .]
  20s...   +1 ficha
[● ● ●]   ← de vuelta a 3 (exactamente 60s después del primer consumo)
```

Protege contra fuerza bruta: máximo ~180 intentos/hora por IP (3/min × 60 min).

---

### 3. Bucket ilimitado (`createUnlimitedBucket`)

```java
// Solo cuando rate-limit está deshabilitado (security.rate-limit.enabled: false — dev por defecto)
capacity = Long.MAX_VALUE
refillIntervally(Long.MAX_VALUE, 1 día)
```

Balde con ~9 × 10¹⁸ fichas. Nunca se agota en la práctica. Evita el `null` check en el filtro.

---

### 4. Bucket exhausto (`createExhaustedBucket`) — implementado, es el fallback de Redis caído

```java
// Cuando Redis lanza una excepción al resolver el bucket (fail-closed)
capacity = 1
refillIntervally(1, Duration.ofDays(1))    // efectivamente no recarga
// + consume inmediatamente la única ficha al crear el bucket
bucket.tryConsume(1);
```

**Visualización:**
```
[●]  ← se crea con 1 ficha
 ↓     bucket.tryConsume(1) la consume al instante
[.]  ← 0 fichas → cualquier petición recibe 429 inmediatamente
```

`resolveBucket`/`resolveLoginBucket` lo devuelven dentro de un `catch (Exception e)` alrededor de
la llamada a `proxyManager.getProxy(...)` — es decir, **no** es para "IPs nuevas cuando un mapa
está lleno" (ese escenario ya no existe, los buckets viven en Redis con expiración automática,
no en un mapa acotado en memoria). Es la respuesta a una pregunta distinta y más importante:
**¿qué pasa si Redis mismo no responde?** Este bucket, efímero y descartado en cada petición, es
esa respuesta — sin consumir memoria ni ejecutar lógica de aplicación adicional.

---

## Parte 2 — Fail-Open vs Fail-Closed en Rate Limiting

### Definiciones

### Fail-Open
Cuando el sistema de control falla o alcanza su límite, **permite el paso** por defecto.
El sistema prioriza **disponibilidad** sobre seguridad.

### Fail-Closed
Cuando el sistema de control falla o alcanza su límite, **bloquea el paso** por defecto.
El sistema prioriza **seguridad** sobre disponibilidad.

---

## Comportamiento cuando Redis no responde

Con los buckets en Redis (no en un mapa acotado en memoria), la pregunta de fail-open vs
fail-closed ya no es "¿qué pasa cuando el mapa de IPs se llena?" — es **"¿qué pasa cuando Redis
mismo falla?"**. `RedisBucketResolver.resolveBucket`/`resolveLoginBucket` envuelven la llamada a
`proxyManager.getProxy(...)` en un `try/catch`; el código real ya decidió esto:

### Fail-Open (lo que el proyecto **no** hace)
```
Redis lanza excepción → catch → devolver bucket ILIMITADO → petición PASA sin límite
```
- Si Redis cae, **todo el tráfico** (legítimo y malicioso) queda sin rate limiting hasta que
  Redis se recupere
- Resultado: una caída de Redis se convierte en una ventana abierta para abuso masivo

### Fail-Closed (lo que el proyecto hace: `createExhaustedBucket()`)
```
Redis lanza excepción → catch → devolver bucket YA AGOTADO → HTTP 429 inmediato
```
- Toda petición recibe 429 mientras Redis no responda — incluidos los clientes legítimos
- La petición nunca llega al controlador ni a la base de datos
- Resultado: una caída de Redis degrada el servicio a "rechaza todo", nunca a "acepta todo sin
  control"

---

## Criterios para elegir

### Elige Fail-Open cuando:

| Criterio | Condición |
|---|---|
| **Tipo de servicio** | Servicio público sin autenticación donde la disponibilidad es crítica (ej. portal de noticias) |
| **Perfil de usuarios** | Alta rotación de IPs únicas legítimas (proxies corporativos, NAT compartido) |
| **Consecuencia del bloqueo** | Bloquear un usuario legítimo tiene alto impacto de negocio |
| **Infraestructura** | Hay un WAF/Cloudflare delante que ya filtra DDoS reales |
| **Disponibilidad de Redis** | Redis es un punto único de falla no aceptable — una caída de Redis no debe poder tumbar el servicio entero |

### Elige Fail-Closed cuando:

| Criterio | Condición |
|---|---|
| **Tipo de servicio** | Servicio con autenticación, API privada o datos sensibles |
| **Perfil de usuarios** | Usuarios conocidos con IPs estables o rangos acotados |
| **Consecuencia del bloqueo** | Un usuario legítimo bloqueado temporalmente es tolerable (máx 2 min hasta la próxima limpieza) |
| **Amenaza real** | El servicio es objetivo de DDoS con IPs únicas (bots, botnets) |
| **Sin WAF delante** | La app es la primera línea de defensa |

---

## Impacto en diferentes actores

| Actor | Fail-Open (Redis caído) | Fail-Closed (Redis caído) — lo que hace el proyecto |
|---|---|---|
| Atacante aprovechando la caída de Redis | Pasa sin restricción ❌ | Bloqueado con 429 ✅ |
| Cualquier cliente legítimo mientras Redis está caído | Pasa normalmente (sin límite) ✅ | Bloqueado con 429 hasta que Redis se recupere ⚠️ |
| Servicio bajo abuso durante la caída | Puede colapsar por sobrecarga ❌ | Se degrada a "rechaza todo", pero no colapsa ✅ |
| Disponibilidad de Redis como punto único de falla | No importa (rate limiting deja de proteger) | Importa: una caída de Redis tumba el rate limiting para todos, aunque el resto de la app siga viva |

---

## Capas de defensa (Defense in Depth)

El rate limiting en aplicación es la **última línea**. La arquitectura ideal es:

```
Internet
   ↓
Cloudflare / CDN          ← geo-blocking, CAPTCHA, reputación de IP
   ↓
WAF (nginx / Traefik)     ← reglas de rate limit por IP a nivel de red
   ↓
Load Balancer             ← distribuye carga, detecta patrones anómalos
   ↓
Aplicación (Bucket4j)     ← rate limit por IP y por endpoint (última línea)
```

Con capas anteriores activas, la propia disponibilidad de Redis se vuelve el punto crítico —
no un mapa en memoria que pueda llenarse. Por eso **fail-closed ante un Redis caído** es la
opción segura: prioriza que el servicio se degrade de forma predecible (rechaza todo) antes que
quede completamente desprotegido.

---

## Recomendación para este proyecto (y lo que ya implementa `RedisBucketResolver`)

**Fail-closed ante un Redis caído** es la elección correcta, y es la que el código ya aplica
(`createExhaustedBucket()` en el `catch` de `resolveBucket`/`resolveLoginBucket`), porque:

1. Es una API con autenticación (usuarios conocidos)
2. El endpoint de login es especialmente sensible (credenciales)
3. El impacto de que Redis esté caído unos segundos y algún request legítimo reciba 429 es
   tolerable
4. El impacto de que Redis caiga y el sistema quede **sin ningún control de tasa** durante ese
   tiempo es crítico — es exactamente cuando más se necesita la protección

No hay una propiedad de capacidad que calibrar del lado de la aplicación (no existe
`max-tracked-ips`): la capacidad relevante ahora es la del propio Redis, dimensionada como
cualquier otra dependencia de infraestructura del despliegue.
