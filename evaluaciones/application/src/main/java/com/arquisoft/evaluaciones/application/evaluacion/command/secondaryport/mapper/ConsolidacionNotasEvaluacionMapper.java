package com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.ConsolidacionNotasEvaluacionEntity;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.PesosFinalizacionEntity;
import com.arquisoft.evaluaciones.domain.evaluacion.ConsolidacionNotasEvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacion.model.PesosFinalizacion;

public final class ConsolidacionNotasEvaluacionMapper {

    private ConsolidacionNotasEvaluacionMapper() {}

    public static ConsolidacionNotasEvaluacionEntity toEntity(ConsolidacionNotasEvaluacionDomain domain) {
        return new ConsolidacionNotasEvaluacionEntity(
                domain.getEvaluacion(),
                domain.getNotaAsesor(),
                domain.getNotaJurados(),
                domain.getPesoAsesor(),
                domain.getPesoJurados(),
                domain.getCantidadJurados());
    }

    public static PesosFinalizacion toDomain(PesosFinalizacionEntity entity) {
        return new PesosFinalizacion(entity.pesoAsesor(), entity.pesoJurado());
    }
}
