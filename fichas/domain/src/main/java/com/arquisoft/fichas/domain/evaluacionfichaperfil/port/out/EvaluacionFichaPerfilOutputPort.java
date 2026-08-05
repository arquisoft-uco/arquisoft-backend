package com.arquisoft.fichas.domain.evaluacionfichaperfil.port.out;

import com.arquisoft.fichas.domain.evaluacionfichaperfil.aggregate.EvaluacionFichaPerfilDomain;

import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.PropietarioEvaluacionCriteria;

import java.util.UUID;

public interface EvaluacionFichaPerfilOutputPort {

    void registrarEvaluacion(EvaluacionFichaPerfilDomain evaluacion);

    boolean existePorId(UUID id);

    boolean existePorRepresentanteYFicha(UUID representanteComiteId, UUID fichaPerfilId);

    boolean esRepresentantePropietario(PropietarioEvaluacionCriteria criteria);
}
