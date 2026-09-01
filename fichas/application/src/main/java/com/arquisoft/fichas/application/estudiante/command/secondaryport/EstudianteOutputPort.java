package com.arquisoft.fichas.application.estudiante.command.secondaryport;

import com.arquisoft.fichas.application.estudiante.command.secondaryport.entity.EstudianteEntity;

import java.util.List;
import java.util.UUID;

public interface EstudianteOutputPort {

    boolean existePorId(UUID id);

    List<EstudianteEntity> buscarPorIds(List<UUID> ids);
}
