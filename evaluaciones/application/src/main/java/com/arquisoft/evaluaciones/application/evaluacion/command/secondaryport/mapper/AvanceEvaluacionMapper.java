package com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.AvanceEvaluacionEntity;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.AvanceEvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacion.AvanceEvaluacionDomain.DatosEntregable;
import com.arquisoft.evaluaciones.domain.evaluacion.AvanceEvaluacionDomain.ProgresoParticipantes;

public final class AvanceEvaluacionMapper {

    private AvanceEvaluacionMapper() {}

    public static AvanceEvaluacionDomain toDomain(AvanceEvaluacionEntity entity) {
        return AvanceEvaluacionDomain.reconstruir(
                entity.evaluacion(),
                EstadoEvaluacion.desde(entity.estado()),
                new DatosEntregable(entity.entregable(), entity.proyecto(), entity.version()),
                new ProgresoParticipantes(
                        entity.juradosVigentes(), entity.juradosEvaluados(), entity.asesorCompleto()));
    }
}
