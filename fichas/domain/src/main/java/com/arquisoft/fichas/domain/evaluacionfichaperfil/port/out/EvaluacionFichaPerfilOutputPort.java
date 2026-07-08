package com.arquisoft.fichas.domain.evaluacionfichaperfil.port.out;

import com.arquisoft.fichas.domain.evaluacionfichaperfil.aggregate.EvaluacionFichaPerfilAggregate;

import java.util.UUID;

public interface EvaluacionFichaPerfilOutputPort {

    void guardar(EvaluacionFichaPerfilAggregate evaluacion);

    boolean existsById(UUID id);

    boolean existsByRepresentanteAndFicha(UUID representanteComiteId, UUID fichaPerfilId);
}
