package com.arquisoft.proyectos.application.estudiante.command.finder.impl;

import com.arquisoft.proyectos.application.estudiante.command.finder.EstudiantePorIdFinder;
import com.arquisoft.proyectos.application.estudiante.command.secondaryport.EstudianteOutputPort;
import com.arquisoft.proyectos.application.estudiante.command.secondaryport.mapper.EstudianteMapper;
import com.arquisoft.proyectos.domain.estudiante.EstudianteDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudiantePorIdFinderImpl implements EstudiantePorIdFinder {

    private final EstudianteOutputPort estudianteOutputPort;

    @Override
    public EstudianteDomain obtener(UUID id) {
        return estudianteOutputPort.obtenerPorId(id).map(EstudianteMapper::toDomain)
                .orElse(EstudianteDomain.VACIO);
    }
}
