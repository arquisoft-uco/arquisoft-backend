package com.arquisoft.usuarios.infrastructure.usuario.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.usuario.query.secondaryport.UsuarioQueryOutputPort;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Adaptador mock temporal del output port read-side de usuario.
 *
 * <p><strong>MOCK TEMPORAL:</strong> El contexto usuarios no tiene un paquete query/ real todavía
 * (solo command/). Este adaptador simula la existencia de usuarios devolviendo siempre {@code true}
 * en {@code existsById(UUID)}, hasta que la feature usuario desarrolle su propio read side.
 *
 * <p><strong>NOTA:</strong> Debe sustituirse cuando usuario tenga query/ con su propio
 * {@code UsuarioJpaQueryEntity} y repository real.
 */
@Component
public class UsuarioQueryOutputAdapter implements UsuarioQueryOutputPort {

    @Override
    public Boolean existsById(UUID id) {
        // TODO: Mock temporal. Sustituir cuando la feature usuario tenga su propio paquete query/ real con UsuarioJpaQueryEntity y repository.
        return true;
    }
}
