package com.arquisoft.proyectos.application.estudiante.command.secondaryport;

import com.arquisoft.proyectos.application.estudiante.command.secondaryport.entity.EstudianteEntity;

import java.util.Optional;
import java.util.UUID;

public interface EstudianteOutputPort {

    void guardar(EstudianteEntity estudiante);

    Optional<EstudianteEntity> obtenerPorId(UUID id);
}
