package com.arquisoft.fichas.application.evaluacionfichaperfil.command.validator;

import com.arquisoft.fichas.domain.evaluacionfichaperfil.EvaluacionFichaPerfilDomain;

public interface RegistrarEvaluacionFichaPerfilValidator {

    void validar(EvaluacionFichaPerfilDomain evaluacion, boolean fichaExiste, boolean representanteExiste,
                 boolean evaluacionYaExiste);
}
