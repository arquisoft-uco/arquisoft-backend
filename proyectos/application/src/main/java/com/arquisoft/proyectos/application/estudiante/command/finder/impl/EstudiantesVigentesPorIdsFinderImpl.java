package com.arquisoft.proyectos.application.estudiante.command.finder.impl;

import com.arquisoft.proyectos.application.estudiante.command.finder.EstudiantesVigentesPorIdsFinder;
import com.arquisoft.proyectos.application.estudiante.command.secondaryport.EstudianteOutputPort;
import com.arquisoft.proyectos.application.estudiante.command.secondaryport.mapper.EstudianteMapper;
import com.arquisoft.proyectos.domain.estudiante.EstudianteDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudiantesVigentesPorIdsFinderImpl implements EstudiantesVigentesPorIdsFinder {

    private final EstudianteOutputPort estudianteOutputPort;

    @Override
    public List<EstudianteDomain> obtener(List<UUID> ids) {
        return estudianteOutputPort.obtenerVigentesPorIds(ids).stream()
                .map(EstudianteMapper::toDomain)
                .toList();
    }
}
