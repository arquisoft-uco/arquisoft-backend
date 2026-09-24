package com.arquisoft.usuarios.application.usuario.command.secondaryport;

import com.arquisoft.usuarios.application.usuario.command.secondaryport.entity.UsuarioEntity;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioOutputPort {

    Optional<UsuarioEntity> obtenerPorId(UUID id);

    void guardar(UsuarioEntity usuario);

    void actualizar(UsuarioEntity usuario);

    boolean existePorIdentificador(String identificador);

    boolean existePorEmail(String email);

    boolean existePorContacto(String contacto);

    boolean existePorIdentificadorEnOtroUsuario(String identificador, UUID usuario);

    boolean existePorEmailEnOtroUsuario(String email, UUID usuario);

    boolean existePorContactoEnOtroUsuario(String contacto, UUID usuario);
}
