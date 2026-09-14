package com.arquisoft.fichas.application.estudiante.command.finder.impl;

import com.arquisoft.fichas.application.estudiante.command.finder.EstudiantePorIdFinder;
import com.arquisoft.fichas.application.estudiante.command.secondaryport.EstudianteOutputPort;
import com.arquisoft.fichas.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudiantePorIdFinderImpl implements EstudiantePorIdFinder {

    private final EstudianteOutputPort estudianteOutputPort;

    @Override
    public Optional<EstudianteEntity> obtener(UUID id) {
        return estudianteOutputPort.obtenerPorId(id);
    }
}
