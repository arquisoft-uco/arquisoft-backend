package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.secondaryport;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.secondaryport.entity.ContextoEvaluacionCuantitativaAsesorEntity;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.secondaryport.entity.EvaluacionCuantitativaAsesorEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface EvaluacionCuantitativaAsesorOutputPort {

    void registrarTodas(List<EvaluacionCuantitativaAsesorEntity> entidades);

    void eliminarPorItems(UUID evaluacionAsesor, Set<UUID> items);

    Set<UUID> consultarItemsRegistrados(UUID evaluacionAsesor, Set<UUID> items);

    Optional<ContextoEvaluacionCuantitativaAsesorEntity> obtenerContexto(UUID evaluacionCuantitativaAsesor);

    Optional<ContextoEvaluacionCuantitativaAsesorEntity> obtenerContextoPorObservacion(UUID observacion);

    Optional<UUID> obtenerItem(UUID evaluacionCuantitativaAsesor);

    void actualizarPuntaje(UUID id, Integer puntaje);
}
