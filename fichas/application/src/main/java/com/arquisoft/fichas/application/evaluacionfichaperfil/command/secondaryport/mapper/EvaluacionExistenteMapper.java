package com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.mapper;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.entity.EvaluacionExistenteEntity;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.EvaluacionExistente;
import com.arquisoft.shared.util.UtilObjeto;

public final class EvaluacionExistenteMapper {

    private EvaluacionExistenteMapper() {}

    public static EvaluacionExistente toDomain(EvaluacionExistenteEntity entity) {
        var estado = UtilObjeto.esNulo(entity.estadoEvaluacionId())
                ? EstadoEvaluacion.VACIO
                : EstadoEvaluacion.desde(entity.estadoEvaluacionId());

        return new EvaluacionExistente(entity.id(), estado);
    }
}
