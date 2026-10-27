package com.arquisoft.artefactos.application.estudiante.command.finder.impl;

import com.arquisoft.artefactos.application.estudiante.command.finder.EstudiantesPorIdsFinder;
import com.arquisoft.artefactos.application.estudiante.command.secondaryport.EstudianteOutputPort;
import com.arquisoft.artefactos.application.estudiante.command.secondaryport.mapper.EstudianteMapper;
import com.arquisoft.artefactos.domain.estudiante.EstudianteDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudiantesPorIdsFinderImpl implements EstudiantesPorIdsFinder {

    private final EstudianteOutputPort estudianteOutputPort;

    @Override
    public List<EstudianteDomain> obtener(List<UUID> ids) {
        return estudianteOutputPort.obtenerPorIds(ids).stream()
                .map(EstudianteMapper::toDomain)
                .toList();
    }
}
