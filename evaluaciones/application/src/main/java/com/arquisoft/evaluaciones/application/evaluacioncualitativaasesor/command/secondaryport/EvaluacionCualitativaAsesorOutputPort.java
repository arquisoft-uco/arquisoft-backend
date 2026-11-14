package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.secondaryport;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.secondaryport.entity.EvaluacionCualitativaAsesorEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface EvaluacionCualitativaAsesorOutputPort {

    void registrarTodas(List<EvaluacionCualitativaAsesorEntity> entidades);

    Set<UUID> consultarItemsRegistrados(UUID evaluacionAsesor, Set<UUID> items);

    boolean existePorItem(UUID item);

    void eliminarPorItems(UUID evaluacionAsesor, Set<UUID> items);

    Optional<EvaluacionCualitativaAsesorEntity> obtenerPorId(UUID id);

    void actualizar(EvaluacionCualitativaAsesorEntity entity);
}
