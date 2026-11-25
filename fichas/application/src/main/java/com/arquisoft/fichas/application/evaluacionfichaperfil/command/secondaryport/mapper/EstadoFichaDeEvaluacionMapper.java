package com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.mapper;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.entity.EstadoFichaDeEvaluacionEntity;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.model.EstadoActualFicha;

public final class EstadoFichaDeEvaluacionMapper {

    private EstadoFichaDeEvaluacionMapper() {}

    public static EstadoActualFicha toDomain(EstadoFichaDeEvaluacionEntity entity) {
        return new EstadoActualFicha(entity.fichaPerfilId(), EstadoFicha.desde(entity.estadoFichaId()));
    }
}
