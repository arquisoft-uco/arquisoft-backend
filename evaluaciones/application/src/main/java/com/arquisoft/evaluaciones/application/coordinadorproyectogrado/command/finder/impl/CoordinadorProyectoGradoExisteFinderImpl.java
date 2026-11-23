package com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.finder.impl;

import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.finder.CoordinadorProyectoGradoExisteFinder;
import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.secondaryport.CoordinadorProyectoGradoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CoordinadorProyectoGradoExisteFinderImpl implements CoordinadorProyectoGradoExisteFinder {

    private final CoordinadorProyectoGradoOutputPort coordinadorProyectoGradoOutputPort;

    @Override
    public Boolean obtener(UUID proyectoGrado) {
        return coordinadorProyectoGradoOutputPort.existePorProyectoGrado(proyectoGrado);
    }
}
