package com.arquisoft.usuarios.application.usuario.command.secondaryport;

import com.arquisoft.usuarios.application.usuario.command.secondaryport.entity.UsuarioEntity;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioOutputPort {

    Optional<UsuarioEntity> obtenerPorId(UUID id);

    void guardar(UsuarioEntity usuario);

    boolean existePorIdentificador(String identificador);

    boolean existePorEmail(String email);

    boolean existePorContacto(String contacto);
}
