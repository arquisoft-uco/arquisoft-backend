package com.arquisoft.fichas.application.estadoevaluacionficha.command.secondaryport;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.secondaryport.entity.EstadoEvaluacionFichaEntity;

import java.util.Optional;
import java.util.UUID;

public interface EstadoEvaluacionFichaOutputPort {

    void registrarEstadoInicial(EstadoEvaluacionFichaEntity estado);

    void agregarEstado(EstadoEvaluacionFichaEntity estado);

    boolean existePorEvaluacionYEstado(UUID evaluacionFichaPerfilId, String estadoEvaluacionId);

    long contarEstadosPorEvaluacion(UUID evaluacionFichaPerfilId);

    boolean existeEstadoEvaluacionPorId(String estadoEvaluacionId);

    Optional<EstadoEvaluacionFichaEntity> obtenerUltimoEstado(UUID evaluacionFichaPerfilId);
}
