package com.arquisoft.proyectos.application.estudiante.command.finder.impl;

import com.arquisoft.proyectos.application.estudiante.command.finder.EstudianteProyectosPorIdFinder;
import com.arquisoft.proyectos.application.estudiante.command.secondaryport.EstudianteOutputPort;
import com.arquisoft.proyectos.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudianteProyectosPorIdFinderImpl implements EstudianteProyectosPorIdFinder {

    private final EstudianteOutputPort estudianteOutputPort;

    @Override
    public Optional<EstudianteEntity> obtener(UUID id) {
        return estudianteOutputPort.obtenerPorId(id);
    }
}
