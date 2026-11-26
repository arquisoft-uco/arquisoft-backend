package com.arquisoft.fichas.application.evaluacionfichaperfil.command.usecase;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.result.InicioEvaluacionFichaPerfilResult;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.EvaluacionFichaPerfilDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface RegistrarEvaluacionFichaPerfilUseCase
        extends UseCase<EvaluacionFichaPerfilDomain, InicioEvaluacionFichaPerfilResult> {}
