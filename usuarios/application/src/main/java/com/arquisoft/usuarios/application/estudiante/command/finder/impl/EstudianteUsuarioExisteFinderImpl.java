package com.arquisoft.usuarios.application.estudiante.command.finder.impl;

import com.arquisoft.usuarios.application.estudiante.command.finder.EstudianteUsuarioExisteFinder;
import com.arquisoft.usuarios.application.estudiante.command.secondaryport.EstudianteOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudianteUsuarioExisteFinderImpl implements EstudianteUsuarioExisteFinder {

    private final EstudianteOutputPort estudianteOutputPort;

    @Override
    public Boolean obtener(UUID usuario) {
        return estudianteOutputPort.existePorUsuario(usuario);
    }
}
