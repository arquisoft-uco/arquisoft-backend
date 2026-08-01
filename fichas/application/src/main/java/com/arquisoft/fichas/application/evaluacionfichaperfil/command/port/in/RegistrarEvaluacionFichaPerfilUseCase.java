package com.arquisoft.fichas.application.evaluacionfichaperfil.command.port.in;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.model.RegistrarEvaluacionFichaPerfilCommand;
import com.arquisoft.shared.usecase.UseCase;

import java.util.UUID;

public interface RegistrarEvaluacionFichaPerfilUseCase extends UseCase<RegistrarEvaluacionFichaPerfilCommand, UUID> {}
