package com.arquisoft.seguridad.domain.usuario.port.out;

import com.arquisoft.seguridad.domain.usuario.aggregate.UsuarioAggregate;

import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida — abstracción de persistencia del aggregate {@link UsuarioAggregate}.
 *
 * <p>La implementación concreta decide el mecanismo de almacenamiento
 * (JPA, en memoria, etc.) sin que el dominio lo conozca.
 */
public interface UsuarioOutputPort {

    /**
     * Persiste un aggregate UsuarioAggregate (creación o actualización).
     */
    void save(UsuarioAggregate usuario);

    /**
     * Busca un usuario por su identificador.
     */
    Optional<UsuarioAggregate> findById(UUID id);
}
