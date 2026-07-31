package com.arquisoft.fichas.domain.estadoevaluacionficha.port.out;

import com.arquisoft.fichas.domain.estadoevaluacionficha.aggregate.EstadoEvaluacionFichaAggregate;

import java.util.Optional;
import java.util.UUID;

public interface EstadoEvaluacionFichaOutputPort {

    void guardar(EstadoEvaluacionFichaAggregate aggregate);

    boolean existePorEvaluacionYEstado(UUID evaluacionFichaPerfilId, String estadoEvaluacionId);

    long contarEstadosPorEvaluacion(UUID evaluacionFichaPerfilId);

    boolean existeEstadoEvaluacionPorId(String estadoEvaluacionId);

    Optional<String> obtenerUltimoEstado(UUID evaluacionFichaPerfilId);
}
