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

## Los 4 tipos de buckets en este proyecto

### 1. Bucket general (`createGeneralBucket`)

```java
// Para cualquier endpoint autenticado
capacity = requestsPerMinute          // ej: 60 en prod
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

### 2. Bucket de login (`createLoginBucket`)

```java
// Solo para POST /auth/login
capacity = loginRequestsPerMinute     // ej: 3 en prod
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
// Solo cuando rate-limit está deshabilitado (dev/test)
capacity = Long.MAX_VALUE
refillIntervally(Long.MAX_VALUE, 1 día)
```

Balde con ~9 × 10¹⁸ fichas. Nunca se agota en la práctica. Evita el `null` check en el filtro.

---

### 4. Bucket exhausto (`createExhaustedBucket`) — pendiente de implementar

```java
// Para IPs nuevas cuando el mapa está lleno (fail-closed)
capacity = 1
refillIntervally(1, Duration.ofDays(1))    // efectivamente no recarga
// + consume inmediatamente la única ficha al crear el bucket
```

**Visualización:**
```
[●]  ← se crea con 1 ficha
 ↓     bucket.tryConsume(1) la consume al instante
[.]  ← 0 fichas → cualquier petición recibe 429 inmediatamente
```

Este bucket NO se guarda en el mapa — se crea y descarta en cada petición de IP desconocida.
No consume memoria. Solo sirve para devolver 429 sin ejecutar lógica de aplicación.

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

## Comportamiento en Rate Limiting con mapa de IPs lleno

### Fail-Open
```
Mapa lleno (10.000 IPs) → IP nueva → bucket LLENO → petición PASA
```
- El atacante con IP nueva obtiene un bucket fresco con fichas completas
- Puede seguir enviando peticiones sin restricción
- El heap no crece (protección OOM), pero la CPU, threads y DB sí se saturan
- Resultado: el servicio puede colapsar por sobrecarga de recursos

### Fail-Closed
```
Mapa lleno (10.000 IPs) → IP nueva → bucket VACÍO → HTTP 429 inmediato
```
- El atacante con IP nueva recibe rechazo directo en el filtro
- La petición nunca llega a la aplicación, controladores ni base de datos
- Los clientes legítimos ya registrados en el mapa siguen operando normalmente
- Resultado: el servicio sobrevive bajo DDoS con IPs únicas

---

## Criterios para elegir

### Elige Fail-Open cuando:

| Criterio | Condición |
|---|---|
| **Tipo de servicio** | Servicio público sin autenticación donde la disponibilidad es crítica (ej. portal de noticias) |
| **Perfil de usuarios** | Alta rotación de IPs únicas legítimas (proxies corporativos, NAT compartido) |
| **Consecuencia del bloqueo** | Bloquear un usuario legítimo tiene alto impacto de negocio |
| **Infraestructura** | Hay un WAF/Cloudflare delante que ya filtra DDoS reales |
| **Límite calibrado** | El `max-tracked-ips` es tan alto que nunca se alcanza en condiciones normales |

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

| Actor | Fail-Open (mapa lleno) | Fail-Closed (mapa lleno) |
|---|---|---|
| Atacante DDoS (IPs nuevas) | Pasa sin restricción ❌ | Bloqueado con 429 ✅ |
| Cliente legítimo conocido (en el mapa) | Pasa normalmente ✅ | Pasa normalmente ✅ |
| Cliente legítimo NUEVO (IP no vista) | Pasa normalmente ✅ | Bloqueado hasta próxima limpieza ⚠️ |
| Servicio bajo DDoS | Colapsa por sobrecarga ❌ | Sobrevive ✅ |
| Heap JVM | Estable ✅ | Estable ✅ |

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

Con capas anteriores activas, el mapa de Bucket4j difícilmente se llena con IPs legítimas,
lo que hace que **fail-closed sea la opción segura** sin impacto real en usuarios normales.

---

## Recomendación para este proyecto

**Fail-closed** es la elección correcta porque:

1. Es una API con autenticación (usuarios conocidos)
2. No hay evidencia de alta rotación de IPs legítimas
3. El endpoint de login es especialmente sensible (credenciales)
4. El impacto de bloquear un usuario nuevo legítimo es mínimo (máx 2 minutos)
5. El impacto de no bloquear un DDoS es crítico (servicio caído para todos)

### Calibración de `max-tracked-ips`

Debe ser mayor que el **pico de usuarios simultáneos legítimos** esperado:

```
max-tracked-ips > pico_usuarios_simultáneos × factor_seguridad(1.5-2x)

Ejemplo: 500 usuarios pico → max-tracked-ips = 1000-1500
```

El default de 10.000 es conservador y adecuado para la mayoría de despliegues universitarios/empresariales medianos.
