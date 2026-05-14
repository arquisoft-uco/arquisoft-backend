package com.arquisoft.seguridad.domain.port.out;

import com.arquisoft.seguridad.domain.model.Usuario;

import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida — abstracción de persistencia del aggregate {@link Usuario}.
 *
 * <p>La implementación concreta decide el mecanismo de almacenamiento
 * (JPA, en memoria, etc.) sin que el dominio lo conozca.
 */
public interface UsuarioRepositoryPort {

    /**
     * Persiste un aggregate Usuario (creación o actualización).
     */
    void save(Usuario usuario);

    /**
     * Busca un usuario por su identificador.
     */
    Optional<Usuario> findById(UUID id);
}
