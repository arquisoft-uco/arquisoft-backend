package com.arquisoft.seguridad.infrastructure.adapter.out.persistence;

import com.arquisoft.seguridad.domain.model.UsuarioAggregate;
import com.arquisoft.seguridad.domain.port.out.UsuarioOutputPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementación en memoria del puerto de persistencia de {@link UsuarioAggregate}.
 *
 * <p><b>TEMPORAL:</b> reemplazar por un adaptador JPA/PostgreSQL cuando se implemente
 * la HU de gestión de usuarios. Este bean simula la persistencia sin base de datos
 * para validar el flujo de eventos con RabbitMQ.
 */
@Slf4j
@Repository
public class UsuarioOutputAdapter implements UsuarioOutputPort {

    private final Map<UUID, UsuarioAggregate> store = new ConcurrentHashMap<>();

    @Override
    public void save(UsuarioAggregate usuario) {
        store.put(usuario.getId(), usuario);
        log.debug("Usuario almacenado en memoria: id={} email={}", usuario.getId(), usuario.getEmail());
    }

    @Override
    public Optional<UsuarioAggregate> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }
}
