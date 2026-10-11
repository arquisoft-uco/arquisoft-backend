package com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.mapper;

import com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.entity.PertenenciaObservacionEvaluacionEntity;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.observacionevaluacion.model.PertenenciaObservacionEvaluacion;
import com.arquisoft.shared.util.UtilObjeto;

public final class PertenenciaObservacionEvaluacionMapper {

    private PertenenciaObservacionEvaluacionMapper() {}

    public static PertenenciaObservacionEvaluacion toDomain(PertenenciaObservacionEvaluacionEntity entity) {
        return new PertenenciaObservacionEvaluacion(
                entity.evaluacionFichaPerfilId(),
                entity.esPropietario(),
                ultimoEstado(entity));
    }

    private static EstadoEvaluacion ultimoEstado(PertenenciaObservacionEvaluacionEntity entity) {
        return UtilObjeto.esNulo(entity.estadoEvaluacionId())
                ? EstadoEvaluacion.VACIO
                : EstadoEvaluacion.desde(entity.estadoEvaluacionId());
    }
}
