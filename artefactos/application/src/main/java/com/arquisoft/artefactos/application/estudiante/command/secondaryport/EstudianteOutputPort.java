package com.arquisoft.artefactos.application.estudiante.command.secondaryport;

import com.arquisoft.artefactos.application.estudiante.command.secondaryport.entity.EstudianteEntity;

import java.util.List;
import java.util.UUID;

public interface EstudianteOutputPort {

    List<EstudianteEntity> obtenerPorIds(List<UUID> ids);
}
