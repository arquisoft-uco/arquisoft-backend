package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.secondaryport;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.secondaryport.entity.EvaluacionCuantitativaJuradoEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface EvaluacionCuantitativaJuradoOutputPort {

    Optional<EvaluacionCuantitativaJuradoEntity> obtenerPorId(UUID id);

    void actualizar(EvaluacionCuantitativaJuradoEntity evaluacionCuantitativaJurado);

    boolean existePorItem(UUID item);

    Set<UUID> consultarIdsPorEvaluacionJurado(UUID evaluacionJurado, Set<UUID> evaluaciones);

    void eliminarPorIds(UUID evaluacionJurado, Set<UUID> evaluaciones);

    void registrarTodas(List<EvaluacionCuantitativaJuradoEntity> entidades);

    Set<UUID> consultarItemsRegistrados(UUID evaluacionJurado, Set<UUID> items);
}
