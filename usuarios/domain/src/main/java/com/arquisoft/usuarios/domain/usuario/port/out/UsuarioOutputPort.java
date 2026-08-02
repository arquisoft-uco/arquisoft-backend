package com.arquisoft.usuarios.domain.usuario.port.out;

import com.arquisoft.usuarios.domain.usuario.aggregate.UsuarioAggregate;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioOutputPort {

    void save(UsuarioAggregate usuario);

    Optional<UsuarioAggregate> findById(UUID id);

    boolean existePorEmail(String email);
}
