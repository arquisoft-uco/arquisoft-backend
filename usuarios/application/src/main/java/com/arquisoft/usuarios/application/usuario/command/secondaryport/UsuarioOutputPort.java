package com.arquisoft.usuarios.application.usuario.command.secondaryport;

import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioOutputPort {

    void save(UsuarioDomain usuario);

    Optional<UsuarioDomain> findById(UUID id);

    boolean existePorEmail(String email);
}
