package com.arquisoft.usuarios.application.usuario.query.secondaryport;

import com.arquisoft.usuarios.application.usuario.query.secondaryport.entity.UsuarioAccesoEntity;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioAccesoQueryOutputPort {

    Optional<UsuarioAccesoEntity> obtenerPorId(UUID usuario);
}
