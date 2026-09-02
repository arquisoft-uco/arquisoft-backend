package com.arquisoft.fichas.application.estudiante.command.finder.impl;

import com.arquisoft.fichas.application.estudiante.command.finder.EstudiantesFinder;
import com.arquisoft.fichas.application.estudiante.command.secondaryport.EstudianteOutputPort;
import com.arquisoft.fichas.application.estudiante.command.secondaryport.mapper.EstudianteMapper;
import com.arquisoft.fichas.domain.estudiante.EstudianteDomain;
import com.arquisoft.shared.util.UtilColeccion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudiantesFinderImpl implements EstudiantesFinder {

    private final EstudianteOutputPort estudianteOutputPort;

    @Override
    public List<EstudianteDomain> obtener(List<UUID> estudiantes) {
        if (UtilColeccion.esVaciaONula(estudiantes)) {
            return List.of();
        }
        return estudianteOutputPort.buscarPorIds(estudiantes).stream()
                .map(EstudianteMapper::toDomain)
                .toList();
    }
}
