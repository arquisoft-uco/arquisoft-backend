package com.arquisoft.fichas.application.evaluacionfichaperfil.command.validator;

import com.arquisoft.fichas.domain.evaluacionfichaperfil.aggregate.EvaluacionFichaPerfilAggregate;

public interface RegistrarEvaluacionFichaPerfilValidator {

    void validar(EvaluacionFichaPerfilAggregate evaluacion);
}
