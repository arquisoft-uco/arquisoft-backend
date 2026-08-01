package com.arquisoft.fichas.application.estadoevaluacionficha.command.port.in;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.model.AgregarEstadoEvaluacionFichaCommand;
import com.arquisoft.shared.usecase.UseCase;

import java.util.UUID;

public interface AgregarEstadoEvaluacionFichaUseCase extends UseCase<AgregarEstadoEvaluacionFichaCommand, UUID> {}
