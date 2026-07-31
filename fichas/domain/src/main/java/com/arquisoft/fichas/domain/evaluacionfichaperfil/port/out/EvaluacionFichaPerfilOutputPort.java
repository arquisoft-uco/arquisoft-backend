package com.arquisoft.fichas.domain.evaluacionfichaperfil.port.out;

import com.arquisoft.fichas.domain.evaluacionfichaperfil.aggregate.EvaluacionFichaPerfilAggregate;

import java.util.UUID;

public interface EvaluacionFichaPerfilOutputPort {

    void guardar(EvaluacionFichaPerfilAggregate evaluacion);

    boolean existePorId(UUID id);

    boolean existePorRepresentanteYFicha(UUID representanteComiteId, UUID fichaPerfilId);
}
