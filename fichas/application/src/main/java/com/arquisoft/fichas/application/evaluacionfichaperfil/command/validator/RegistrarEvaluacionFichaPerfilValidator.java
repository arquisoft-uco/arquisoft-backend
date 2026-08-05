package com.arquisoft.fichas.application.evaluacionfichaperfil.command.validator;

import com.arquisoft.fichas.domain.evaluacionfichaperfil.aggregate.EvaluacionFichaPerfilDomain;

public interface RegistrarEvaluacionFichaPerfilValidator {

    void validar(EvaluacionFichaPerfilDomain evaluacion);
}
