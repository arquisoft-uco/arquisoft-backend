package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.secondaryport;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.secondaryport.entity.EvaluacionCualitativaAsesorEntity;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface EvaluacionCualitativaAsesorOutputPort {

    void registrarTodas(List<EvaluacionCualitativaAsesorEntity> entidades);

    Set<UUID> consultarItemsRegistrados(UUID evaluacionAsesor, Set<UUID> items);
}
