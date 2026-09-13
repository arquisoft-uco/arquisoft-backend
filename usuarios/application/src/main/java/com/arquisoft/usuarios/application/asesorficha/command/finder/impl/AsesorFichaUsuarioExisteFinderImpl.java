package com.arquisoft.usuarios.application.asesorficha.command.finder.impl;

import com.arquisoft.usuarios.application.asesorficha.command.finder.AsesorFichaUsuarioExisteFinder;
import com.arquisoft.usuarios.application.asesorficha.command.secondaryport.AsesorFichaOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorFichaUsuarioExisteFinderImpl implements AsesorFichaUsuarioExisteFinder {

    private final AsesorFichaOutputPort asesorFichaOutputPort;

    @Override
    public Boolean obtener(UUID usuario) {
        return asesorFichaOutputPort.existePorUsuario(usuario);
    }
}
