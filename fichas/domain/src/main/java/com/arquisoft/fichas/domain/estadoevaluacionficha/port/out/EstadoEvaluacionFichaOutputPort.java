package com.arquisoft.fichas.domain.estadoevaluacionficha.port.out;

import com.arquisoft.fichas.domain.estadoevaluacionficha.aggregate.EstadoEvaluacionFichaDomain;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;

import java.util.Optional;
import java.util.UUID;

public interface EstadoEvaluacionFichaOutputPort {

    void registrarEstadoInicial(EstadoEvaluacionFichaDomain estado);

    void agregarEstado(EstadoEvaluacionFichaDomain estado);

    boolean existePorEvaluacionYEstado(UUID evaluacionFichaPerfilId, String estadoEvaluacionId);

    long contarEstadosPorEvaluacion(UUID evaluacionFichaPerfilId);

    boolean existeEstadoEvaluacionPorId(String estadoEvaluacionId);

    Optional<EstadoEvaluacion> obtenerUltimoEstado(UUID evaluacionFichaPerfilId);
}
