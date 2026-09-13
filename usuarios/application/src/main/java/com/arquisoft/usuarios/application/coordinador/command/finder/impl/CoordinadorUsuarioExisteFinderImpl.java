package com.arquisoft.usuarios.application.coordinador.command.finder.impl;

import com.arquisoft.usuarios.application.coordinador.command.finder.CoordinadorUsuarioExisteFinder;
import com.arquisoft.usuarios.application.coordinador.command.secondaryport.CoordinadorOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CoordinadorUsuarioExisteFinderImpl implements CoordinadorUsuarioExisteFinder {

    private final CoordinadorOutputPort coordinadorOutputPort;

    @Override
    public Boolean obtener(UUID usuario) {
        return coordinadorOutputPort.existePorUsuario(usuario);
    }
}
