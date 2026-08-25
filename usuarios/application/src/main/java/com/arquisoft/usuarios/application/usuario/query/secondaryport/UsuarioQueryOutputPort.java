package com.arquisoft.usuarios.application.usuario.query.secondaryport;

import java.util.UUID;

/**
 * Output port read-side de usuario.
 *
 * <p><strong>NOTA:</strong> Este puerto es un stub temporal. El contexto usuarios no tiene un
 * paquete query/ real todavía (solo command/). Este puerto + su adaptador se mockean temporalmente
 * hasta que la feature usuario desarrolle su propio read side. El adaptador devuelve siempre
 * {@code true} en {@code existsById(UUID)}, simulando que todo usuario existe. Debe sustituirse
 * cuando usuario tenga query/.
 */
public interface UsuarioQueryOutputPort {

    /**
     * Verifica si existe un usuario con el id dado.
     *
     * <p><strong>MOCK TEMPORAL:</strong> El adaptador actual siempre devuelve {@code true}.
     *
     * @param id el id del usuario
     * @return {@code true} si el usuario existe, {@code false} en caso contrario
     */
    Boolean existsById(UUID id);
}
