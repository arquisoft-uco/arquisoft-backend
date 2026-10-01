package com.arquisoft.usuarios.application.usuario.command.finder.impl;

import com.arquisoft.usuarios.application.usuario.command.finder.EmailUsuarioExisteFinder;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.UsuarioOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailUsuarioExisteFinderImpl implements EmailUsuarioExisteFinder {

    private final UsuarioOutputPort usuarioOutputPort;

    @Override
    public Boolean obtener(String email) {
        return usuarioOutputPort.existePorEmail(email);
    }
}
