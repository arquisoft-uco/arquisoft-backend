package com.arquisoft.usuarios.application.asesor.command.finder.impl;

import com.arquisoft.usuarios.application.asesor.command.finder.AsesorUsuarioExisteFinder;
import com.arquisoft.usuarios.application.asesor.command.secondaryport.AsesorOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorUsuarioExisteFinderImpl implements AsesorUsuarioExisteFinder {

    private final AsesorOutputPort asesorOutputPort;

    @Override
    public Boolean obtener(UUID usuario) {
        return asesorOutputPort.existePorUsuario(usuario);
    }
}
