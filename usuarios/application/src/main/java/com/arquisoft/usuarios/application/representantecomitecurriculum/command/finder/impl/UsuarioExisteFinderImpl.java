package com.arquisoft.usuarios.application.representantecomitecurriculum.command.finder.impl;

import com.arquisoft.usuarios.application.representantecomitecurriculum.command.finder.UsuarioExisteFinder;
import com.arquisoft.usuarios.application.usuario.query.secondaryport.UsuarioQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UsuarioExisteFinderImpl implements UsuarioExisteFinder {

    private final UsuarioQueryOutputPort usuarioQueryOutputPort;

    @Override
    public Boolean obtener(UUID usuario) {
        return usuarioQueryOutputPort.existsById(usuario);
    }
}
