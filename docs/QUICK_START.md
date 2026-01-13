# 🚀 Guía de Inicio Rápido - Arquisoft Backend

Esta guía te ayudará a comenzar con el desarrollo del backend de Arquisoft basado en Arquitectura Hexagonal Modular.

## ⚡ 5 Pasos para Empezar

### Paso 1: Clonar el Repositorio
```bash
git clone <url-del-repo>
cd arquisoft-backend
```

### Paso 2: Limpiar Módulos Antiguos (Opcional)
Los módulos `ventas/` y `seguridad/` del prototipo anterior aún existen. Puedes eliminarlos:

```bash
# En Windows (PowerShell)
Remove-Item -Recurse -Force "ventas"
Remove-Item -Recurse -Force "seguridad"

# En Linux/Mac
./cleanup-old-modules.sh
```

O simplemente ignóralos en tu branch de trabajo.

### Paso 3: Levantar Servicios con Docker Compose
```bash
docker-compose up -d
```

Espera ~30 segundos para que todos los servicios se inicien:
- PostgreSQL: `localhost:5432`
- RabbitMQ: `localhost:5672` (UI: `localhost:15672`)
- Redis: `localhost:6379`
- Keycloak: `localhost:8081`
- Nextcloud: `localhost:8082`

### Paso 4: Compilar el Proyecto
```bash
# Windows/Mac
./gradlew build

# Linux
chmod +x gradlew && ./gradlew build
```

### Paso 5: Ejecutar la Aplicación
```bash
./gradlew bootRun
```

La aplicación estará disponible en: **http://localhost:8080/api**

---

## 🏗️ Estructura Rápida

```
shared/              ← Componentes reutilizables
├── domain/          ← Eventos, excepciones, value objects
└── infrastructure/  ← RabbitMQ, BD, logging

usuarios/            ← CONTEXTO 1: Usuarios
├── domain/          ← Modelos, puertos
├── application/     ← Casos de uso, DTOs
└── infrastructure/  ← Controladores, repositorios

fichas/              ← CONTEXTO 2: Fichas
proyectos/          ← CONTEXTO 3: Proyectos
artefactos/         ← CONTEXTO 4: Artefactos
... (8 contextos más)
```

---

## 📊 Acceso a Servicios

| Servicio | URL | Usuario | Contraseña |
|----------|-----|--------|-----------|
| **Backend** | http://localhost:8080/api | - | - |
| **RabbitMQ** | http://localhost:15672 | guest | guest |
| **Keycloak** | http://localhost:8081/admin | admin | admin |
| **Nextcloud** | http://localhost:8082 | admin | admin123 |

---

## 💻 Comandos Útiles

```bash
# Compilar
./gradlew build

# Ejecutar
./gradlew bootRun

# Tests
./gradlew test

# Compilar un módulo específico
./gradlew usuarios:build

# Ver estructura de módulos
./gradlew projects

# Limpar caché
./gradlew clean

# Ver logs
docker logs -f arquisoft-backend
```

---

## 🧪 Crear un Nuevo Caso de Uso

### Ejemplo: Crear usuario

#### 1. Definir Puerto de Entrada en Domain
```java
// usuarios/domain/src/main/.../port/in/CrearUsuarioUseCase.java
public interface CrearUsuarioUseCase {
    Usuario crear(Usuario usuario);
}
```

#### 2. Definir Entidad en Domain
```java
// usuarios/domain/src/main/.../model/Usuario.java
public class Usuario extends AggregateRoot {
    private Long id;
    private Email email;
    private String nombre;
    
    public static Usuario crear(Email email, String nombre) {
        Usuario usuario = new Usuario();
        usuario.registerEvent(new UsuarioCreadoEvent(
            usuario.getId(), email.getValue(), nombre
        ));
        return usuario;
    }
}
```

#### 3. Implementar UseCase en Application
```java
// usuarios/application/src/main/.../usecase/CrearUsuarioUseCaseImpl.java
@Service
@RequiredArgsConstructor
public class CrearUsuarioUseCaseImpl implements CrearUsuarioUseCase {
    private final UsuarioRepositoryPort repository;
    private final EventPublisher eventPublisher;
    
    @Override
    public Usuario crear(Usuario usuario) {
        Usuario saved = repository.save(usuario);
        eventPublisher.publishAll(saved.pullDomainEvents());
        return saved;
    }
}
```

#### 4. Crear Controller en Infrastructure
```java
// usuarios/infrastructure/src/main/.../adapter/in/UsuarioController.java
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    private final CrearUsuarioUseCase crearUsuarioUseCase;
    
    @PostMapping
    public ResponseEntity<UsuarioDTO> crear(@RequestBody UsuarioDTO dto) {
        Usuario usuario = crearUsuarioUseCase.crear(dto.toDomain());
        return ResponseEntity.status(201).body(UsuarioDTO.fromDomain(usuario));
    }
}
```

#### 5. Crear Repositorio en Infrastructure
```java
// usuarios/infrastructure/src/main/.../adapter/out/UsuarioRepositoryAdapter.java
@Repository
@RequiredArgsConstructor
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {
    private final JdbcTemplate jdbcTemplate;
    
    @Override
    public Usuario save(Usuario usuario) {
        String sql = "INSERT INTO usuarios.usuario (nombre, email) VALUES (?, ?)";
        jdbcTemplate.update(sql, usuario.getNombre(), usuario.getEmail().getValue());
        return usuario;
    }
}
```

#### 6. Event Listener en Otro Contexto (Opcional)
```java
// proyectos/infrastructure/src/main/.../adapter/in/ProyectosEventListener.java
@Component
@RequiredArgsConstructor
public class ProyectosEventListener {
    
    @RabbitListener(bindings = @QueueBinding(
        value = @Queue("proyectos.usuarios-events"),
        exchange = @Exchange("arquisoft.events"),
        key = "usuarios.creado"
    ))
    public void onUsuarioCreado(String message) {
        // Procesar evento de usuario creado
        // Ej: crear proyecto inicial, enviar email, etc.
    }
}
```

---

## 🐛 Solucionar Problemas

### "Connection refused" en BD
```bash
# Verifica que PostgreSQL está levantado
docker ps | grep postgres

# Si no está levantado
docker-compose up -d postgres
```

### "Cannot get a connection, pool error"
Espera un minuto para que PostgreSQL esté listo, luego reinicia la aplicación.

### RabbitMQ no responde
```bash
# Reiniciar RabbitMQ
docker-compose restart rabbitmq
```

### Puertos ocupados
Si un puerto está ocupado, modifica `docker-compose.yml`:
```yaml
ports:
  - "5433:5432"  # Cambiar puerto local de 5432 a 5433
```

---

## 📚 Documentación Completa

- **README.md** - Documentación completa del proyecto
- **ARQUITECTURA_Y_ESTRUCTURA.md** - Arquitectura hexagonal y ejemplos
- **ARQUITECTURA_ASINCRONICO_ARQUISOFT.md** - Arquitectura asincrónica con eventos
- **RESTRUCTURACION_COMPLETADA.md** - Resumen de cambios realizados

---

## 🎯 Checklist de Desarrollo

- [ ] Clonar el repositorio
- [ ] Levantar Docker Compose
- [ ] Compilar el proyecto
- [ ] Ejecutar la aplicación
- [ ] Verificar que la aplicación responde (GET /api/actuator/health)
- [ ] Leer la arquitectura en los documentos
- [ ] Crear primer modelo de dominio
- [ ] Crear primer caso de uso
- [ ] Crear tests unitarios
- [ ] Crear evento de dominio
- [ ] Configurar listener de eventos

---

## 🤔 Preguntas Frecuentes

**P: ¿Por dónde empiezo a implementar?**
R: Comienza con el contexto `usuarios/` que ya tiene estructura base. Implementa los modelos, puertos y casos de uso básicos.

**P: ¿Cómo agrego un nuevo contexto?**
R: Copia la estructura de `usuarios/` a un nuevo directorio, cambia los nombres de paquetes. Actualiza `settings.gradle`.

**P: ¿Qué es RabbitMQ?**
R: Es un message broker que permite comunicación asincrónica entre contextos. Los eventos se publican en RabbitMQ y otros contextos los consumen sin bloqueo.

**P: ¿Necesito modificar application.yml?**
R: Solo si cambias las credenciales de BD o puertos de servicios. Por defecto, está configurado para localhost.

**P: ¿Cómo testeo?**
R: Usa JUnit 5 + Mockito. Los tests deben mockear los puertos de salida. Ver ejemplos en ARQUITECTURA_Y_ESTRUCTURA.md.

---

## 🚢 Deploy

Cuando estés listo para producción:

1. **Build Docker Image**:
   ```bash
   docker build -t arquisoft-backend:1.0.0 .
   ```

2. **Push a Registry**:
   ```bash
   docker push myregistry/arquisoft-backend:1.0.0
   ```

3. **Deploy a Kubernetes** (ver ejemplo en README.md)

---

## 📞 Soporte

Consulta la documentación completa en los archivos markdown del proyecto.

---

**Generado**: 12 de Enero, 2026  
**Versión**: 1.0.0
