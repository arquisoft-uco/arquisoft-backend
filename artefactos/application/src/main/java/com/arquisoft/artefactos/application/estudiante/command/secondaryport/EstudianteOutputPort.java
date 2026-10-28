package com.arquisoft.artefactos.application.estudiante.command.secondaryport;

import com.arquisoft.artefactos.application.estudiante.command.secondaryport.entity.EstudianteEntity;

import java.util.Optional;
import java.util.UUID;

public interface EstudianteOutputPort {

    Optional<EstudianteEntity> obtenerPorId(UUID id);
}
