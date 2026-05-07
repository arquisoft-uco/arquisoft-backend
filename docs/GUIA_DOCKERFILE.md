# Dockerfile

## Segmento 1 — Base image del build stage

```dockerfile
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /build
```

**Qué hace**: define la imagen base para compilar. `eclipse-temurin` es la distribución oficial de OpenJDK mantenida por la Eclipse Foundation (la más recomendada para producción). `-alpine` usa Alpine Linux (~5MB) en vez de Debian/Ubuntu, reduciendo la superficie de ataque. `AS builder` nombra este stage para referenciarlo después.

**¿Es seguro?** ✅ Sí. Imagen oficial, base mínima, JDK solo en el stage de build (no llega al contenedor final).

---

## Segmento 2 — Copiar el código fuente

```dockerfile
COPY . .
```

**Qué hace**: copia el proyecto al contexto de build. El .dockerignore actúa como filtro — excluye .env, .env.properties, .git, build, docs, etc. antes de que este `COPY` se ejecute.

**¿Es seguro?** ✅ Sí, **gracias al .dockerignore**. Sin él, credenciales y el historial git entrarían a la imagen (OWASP A02).

---

## Segmento 3 — Compilar la aplicación

```dockerfile
RUN chmod +x gradlew && \
    ./gradlew build -x test --no-daemon && \
    find build/libs -maxdepth 1 -name "*.jar" ! -name "*-plain.jar" \
         -exec cp {} app.jar \;
```

**Qué hace** (todo en un solo `RUN` para generar una sola capa):
- `chmod +x gradlew` — da permiso de ejecución al wrapper de Gradle
- `./gradlew build -x test --no-daemon` — compila sin ejecutar tests y sin el daemon de Gradle (el daemon consume memoria extra innecesaria en CI/Docker)
- `find ... ! -name "*-plain.jar"` — Spring Boot genera dos `.jar`: el ejecutable y uno `-plain.jar` (sin dependencias). El `find` copia solo el ejecutable a `app.jar`

**¿Es seguro?** ✅ Sí. El `-x test` es intencional para builds de imagen (los tests se corren en CI por separado). Un solo `RUN` evita capas intermedias con estado transitorio.

---

## Segmento 4 — Base image del runtime stage

```dockerfile
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
```

**Qué hace**: **nuevo stage limpio** con solo el JRE (Java Runtime Environment). El JDK completo del stage anterior se descarta — no llega al contenedor final. El JRE no incluye `javac`, `jar`, `jshell` ni herramientas de build.

**¿Es seguro?** ✅ Sí. Eliminar el JDK reduce la superficie de ataque — un atacante con acceso al contenedor no puede compilar código ni usar herramientas de desarrollo.

---

## Segmento 5 — Usuario no-root

```dockerfile
RUN addgroup -S arquisoft && \
    adduser -S -u 1000 -G arquisoft arquisoft && \
    mkdir -p /app/logs /var/log/arquisoft && \
    chown arquisoft:arquisoft /app/logs /var/log/arquisoft
```

**Qué hace**:
- `-S` crea grupo/usuario de **sistema** (sin contraseña, sin shell interactivo, sin directorio home con permisos)
- `-u 1000` asigna UID fijo y predecible (facilita auditoría y mapeo de permisos en volúmenes)
- `mkdir -p` + `chown` crea los directorios de logs que Logback necesita en dev y prod, con propietario correcto

**¿Es seguro?** ✅ Sí. Aborda directamente **OWASP A05 (Security Misconfiguration)** — por defecto los contenedores corren como `root`, lo que significa que un proceso comprometido dentro del contenedor tiene privilegios de root sobre el sistema de archivos del host si hay escape del contenedor.

---

## Segmento 6 — Copiar el JAR

```dockerfile
COPY --chown=arquisoft:arquisoft --from=builder /build/app.jar app.jar
```

**Qué hace**: copia **solo el JAR** desde el stage builder al runtime. `--chown` asigna propietario en el mismo comando, evitando un `RUN chown` adicional que crearía una capa extra.

**¿Es seguro?** ✅ Sí. Solo el artefacto compilado pasa al contenedor final — ni código fuente, ni Gradle, ni credenciales del build.

---

## Segmento 7 — Cambiar al usuario no-root

```dockerfile
USER arquisoft
```

**Qué hace**: a partir de aquí, todos los comandos y el proceso principal del contenedor corren como `arquisoft` (UID 1000), no como `root`.

**¿Es seguro?** ✅ Sí. Es la línea más crítica de seguridad del Dockerfile. Sin ella, todo lo anterior (crear el usuario) no tiene efecto.

---

## Segmento 8 — Puerto expuesto

```dockerfile
EXPOSE 8080
```

**Qué hace**: documenta que el contenedor escucha en el puerto 8080. Es **solo documentación** — no abre el puerto realmente; eso lo hace `docker run -p 8080:8080` o `--network host`.

**¿Es seguro?** ✅ Sí. No representa riesgo por sí mismo.

---

## Segmento 9 — Health check

```dockerfile
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget -q --spider http://localhost:8080/api/actuator/health || exit 1
```

**Qué hace**: Docker verifica cada 30s si la app está viva llamando al endpoint de salud. `--start-period=60s` da margen para que Spring Boot arranque (conexiones a 7 DBs + Flyway tarda ~34s). `wget --spider` hace HEAD request sin descargar el body.

**¿Es seguro?** ✅ Sí. Usa `wget` (incluido en Alpine) en vez de `curl` que no viene preinstalado. La URL apunta a `localhost` dentro del contenedor, nunca sale a la red.

---

## Segmento 10 — Punto de entrada JVM

```dockerfile
ENTRYPOINT ["java", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-jar", "app.jar"]
```

**Qué hace** cada flag:

| Flag | Propósito |
|---|---|
| `-Djava.security.egd=file:/dev/./urandom` | Evita bloqueo al generar números aleatorios. Sin esto la JVM lee de random que puede bloquearse si el pool de entropía está vacío — común en contenedores sin hardware de entropía |
| `-XX:+UseContainerSupport` | La JVM lee los límites de CPU y memoria del cgroup del contenedor en vez de los del host. Sin esto, Spring Boot ve 64GB de RAM si el host tiene 64GB, aunque el contenedor tenga límite de 512MB |
| `-XX:MaxRAMPercentage=75.0` | El heap JVM usa máximo el 75% de la RAM del contenedor. El 25% restante queda para el OS, Metaspace y threads off-heap |
| `["java", "-jar", "app.jar"]` | Forma array (exec form) — el proceso `java` es PID 1 directamente, recibe señales del OS (SIGTERM para graceful shutdown). La forma string `"java -jar app.jar"` lo envuelve en `/bin/sh -c` y las señales no llegan al proceso Java |

**¿Es seguro?** ✅ Sí. La exec form (array) es la práctica correcta. Los flags JVM son de optimización y no representan riesgo de seguridad.