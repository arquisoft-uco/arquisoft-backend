package com.arquisoft.solicitudes.application.usuario.command.secondaryport;

import com.arquisoft.solicitudes.application.usuario.command.secondaryport.entity.UsuarioEntity;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioOutputPort {

    Optional<UsuarioEntity> buscarPorId(UUID id);

    void guardar(UsuarioEntity usuario);

    void actualizar(UsuarioEntity usuario);
}
