package com.arquisoft.fichas.application.evaluacionfichaperfil.command.usecase;

import com.arquisoft.fichas.domain.evaluacionfichaperfil.aggregate.EvaluacionFichaPerfilDomain;
import com.arquisoft.shared.usecase.UseCase;

import java.util.UUID;

public interface RegistrarEvaluacionFichaPerfilUseCase extends UseCase<EvaluacionFichaPerfilDomain, UUID> {}
