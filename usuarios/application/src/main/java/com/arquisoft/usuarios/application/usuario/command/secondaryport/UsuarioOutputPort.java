package com.arquisoft.usuarios.application.usuario.command.secondaryport;

import com.arquisoft.usuarios.application.usuario.command.secondaryport.entity.UsuarioEntity;

public interface UsuarioOutputPort {

    void guardar(UsuarioEntity usuario);

    boolean existePorEmail(String email);
}
