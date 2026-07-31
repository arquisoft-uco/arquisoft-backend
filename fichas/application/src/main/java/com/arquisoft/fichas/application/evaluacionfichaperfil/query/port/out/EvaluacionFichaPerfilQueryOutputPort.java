package com.arquisoft.fichas.application.evaluacionfichaperfil.query.port.out;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria.PropietarioEvaluacionCriteria;

import java.util.UUID;

public interface EvaluacionFichaPerfilQueryOutputPort {

    boolean existePorId(UUID evaluacionFichaPerfilId);

    boolean esRepresentantePropietario(PropietarioEvaluacionCriteria criteria);
}
