# Guía Rápida de Redis — Arquisoft Backend

Redis en Arquisoft se utiliza para **rate limiting distribuido**, **blacklist de JWT** (logout) y **caching**. Esta guía te muestra cómo inspeccionar y depurar Redis durante el desarrollo.

---

## Acceso al Contenedor Redis

Si ejecutas `docker-compose up`, Redis estará disponible en:

```
Host: localhost
Puerto: 6379
Contraseña: default123
```

### Conectarse con `redis-cli`

Desde tu máquina local (si tienes `redis-cli` instalado):

```bash
redis-cli -h localhost -p 6379 -a default123
```

### Desde el contenedor Docker

```bash
docker exec -it arquisoft-redis redis-cli -a default123
```

---

## Comandos Comunes

### Información General

```bash
PING                           # Verifica conexión (responde PONG)
INFO                           # Estadísticas del servidor
DBSIZE                         # Número total de claves
FLUSHDB                        # ⚠️ Borra TODAS las claves (solo dev!)
FLUSHALL                       # ⚠️ Borra todas las bases de datos
```

### Inspeccionar Claves

```bash
KEYS *                         # Lista TODAS las claves (LENTO en producción)
KEYS "arquisoft:*"            # Filtra claves por patrón
KEYS "arquisoft:blacklist:*"  # Solo blacklist JWT
KEYS "arquisoft:ratelimit:*"  # Solo rate limiting
KEYS "arquisoft:cache:*"      # Solo cache
```

### Consultar Valores

```bash
GET key                        # Obtiene valor de una clave
TYPE key                       # Muestra tipo (string, hash, list, set, zset)
TTL key                        # Tiempo restante en segundos (-1 = sin expiración)
PTTL key                       # Tiempo restante en milisegundos
```

### Eliminar Claves

```bash
DEL key1 key2 key3            # Elimina una o más claves
UNLINK key1 key2              # Elimina de forma asincrónica (más rápido)
EXPIRE key 60                 # Establece expiración (60 segundos)
```

---

## Comportamiento del Sistema

### 1️⃣ Rate Limiting — Buckets

Cuando alguien hace login o solicitudes HTTP, Bucket4j crea buckets en Redis:

```bash
# Login: máximo 3 intentos por minuto por IP
KEYS "arquisoft:ratelimit:login:*"
GET "arquisoft:ratelimit:login:127.0.0.1"

# Endpoints general: máximo 100 intentos por minuto por IP
KEYS "arquisoft:ratelimit:global:*"
GET "arquisoft:ratelimit:global:192.168.1.100"
```

**Verificar estado del bucket:**

```bash
redis-cli -a default123 --raw <<EOF
KEYS "arquisoft:ratelimit:*"
FOREACH key IN KEYS
  TYPE $key
  TTL $key
EOF
```

### 2️⃣ Blacklist JWT — Logout

Cuando un usuario hace logout, su JWT se agrega a la blacklist con un TTL igual a la vida restante del token:

```bash
# Ver todos los tokens bloqueados
KEYS "arquisoft:blacklist:jti:*"

# Consultar un token específico
GET "arquisoft:blacklist:jti:550e8400-e29b-41d4-a716-446655440000"

# Ver TTL (expiración automática)
TTL "arquisoft:blacklist:jti:550e8400-e29b-41d4-a716-446655440000"
```

**Simular logout:**
1. Obtén un token válido (login exitoso)
2. Extrae el `jti` del JWT (decodifica en jwt.io)
3. Después de logout, verifica que esté en la blacklist
4. Intenta usar el token revocado → `401 Unauthorized`

### 3️⃣ Cache — Consultas Lentas

Spring Boot guarda en cache resultados de operaciones frecuentes por 30 minutos:

```bash
# Ver todo lo cacheado
KEYS "arquisoft:cache:*"

# Ejemplo: cache de fichas
KEYS "arquisoft:cache:fichas:*"
GET "arquisoft:cache:fichas:findById:550e8400-e29b-41d4-a716-446655440000"

# TTL del cache (30 minutos = 1800 segundos)
TTL "arquisoft:cache:fichas:findById:550e8400-e29b-41d4-a716-446655440000"

# Invalidar cache de una ficha
DEL "arquisoft:cache:fichas:findById:550e8400-e29b-41d4-a716-446655440000"
```

---

## Notas Importantes

⚠️ **No ejecutes** `FLUSHDB` en producción — eliminará el rate limiting y la seguridad.

✅ **Redis es fail-closed** — si cae, el sistema retorna `503 Service Unavailable` en login y rate limit, priorizando seguridad sobre disponibilidad.

✅ **IPs en buckets** — el rate limiting es por IP del cliente. En Docker, todas las requests locales vienen de `127.0.0.1`.
