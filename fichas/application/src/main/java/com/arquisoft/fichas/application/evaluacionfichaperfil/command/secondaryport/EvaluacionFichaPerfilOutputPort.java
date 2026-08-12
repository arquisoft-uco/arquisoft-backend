package com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport;

import com.arquisoft.fichas.domain.evaluacionfichaperfil.EvaluacionFichaPerfilDomain;

import java.util.UUID;

public interface EvaluacionFichaPerfilOutputPort {

    void registrarEvaluacion(EvaluacionFichaPerfilDomain evaluacion);

    boolean existePorId(UUID id);

    boolean existePorRepresentanteYFicha(UUID representanteComiteId, UUID fichaPerfilId);

    boolean esRepresentantePropietario(UUID evaluacionFichaPerfil, UUID representanteComite);
}
