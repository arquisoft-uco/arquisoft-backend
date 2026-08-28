package com.arquisoft.solicitudes.application.solicitud.command.finder.impl;

import com.arquisoft.solicitudes.application.solicitud.command.finder.UsuarioExisteFinder;
import com.arquisoft.solicitudes.application.usuario.command.secondaryport.UsuarioOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UsuarioExisteFinderImpl implements UsuarioExisteFinder {

    private final UsuarioOutputPort usuarioOutputPort;

    @Override
    public Boolean obtener(UUID usuario) {
        return usuarioOutputPort.existePorId(usuario);
    }
}
