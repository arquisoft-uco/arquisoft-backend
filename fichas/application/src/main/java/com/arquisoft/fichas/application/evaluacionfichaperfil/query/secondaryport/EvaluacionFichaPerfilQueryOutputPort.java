package com.arquisoft.fichas.application.evaluacionfichaperfil.query.secondaryport;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria.PropietarioEvaluacionCriteria;

import java.util.UUID;

public interface EvaluacionFichaPerfilQueryOutputPort {

    boolean existePorId(UUID evaluacionFichaPerfilId);

    boolean esRepresentantePropietario(PropietarioEvaluacionCriteria criteria);
}
