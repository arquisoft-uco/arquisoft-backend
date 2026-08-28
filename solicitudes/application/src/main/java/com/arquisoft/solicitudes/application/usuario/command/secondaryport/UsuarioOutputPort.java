package com.arquisoft.solicitudes.application.usuario.command.secondaryport;

import com.arquisoft.solicitudes.application.usuario.command.secondaryport.entity.UsuarioEntity;

import java.util.UUID;

public interface UsuarioOutputPort {

    boolean existePorId(UUID id);

    void registrar(UsuarioEntity usuario);

    void actualizar(UsuarioEntity usuario);
}
