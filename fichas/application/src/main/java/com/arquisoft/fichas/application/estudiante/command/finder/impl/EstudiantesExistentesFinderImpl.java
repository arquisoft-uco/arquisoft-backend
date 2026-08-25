package com.arquisoft.fichas.application.estudiante.command.finder.impl;

import com.arquisoft.fichas.application.estudiante.command.finder.EstudiantesExistentesFinder;
import com.arquisoft.fichas.application.estudiante.command.secondaryport.EstudianteOutputPort;
import com.arquisoft.shared.util.UtilColeccion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudiantesExistentesFinderImpl implements EstudiantesExistentesFinder {

    private final EstudianteOutputPort estudianteOutputPort;

    @Override
    public List<UUID> obtener(List<UUID> estudiantes) {
        if (UtilColeccion.esVaciaONula(estudiantes)) {
            return List.of();
        }
        return estudiantes.stream()
                .filter(estudianteOutputPort::existePorId)
                .toList();
    }
}
