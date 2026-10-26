package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.secondaryport;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.secondaryport.entity.EvaluacionCuantitativaAsesorEntity;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface EvaluacionCuantitativaAsesorOutputPort {

    void registrarTodas(List<EvaluacionCuantitativaAsesorEntity> entidades);

    Set<UUID> consultarItemsRegistrados(UUID evaluacionAsesor, Set<UUID> items);
}
