package com.arquisoft.fichas.application.estadoevaluacionficha.command.usecase;

import com.arquisoft.fichas.domain.estadoevaluacionficha.AgregacionEstadoEvaluacionFichaDomain;
import com.arquisoft.shared.usecase.UseCase;

import java.util.UUID;

public interface AgregarEstadoEvaluacionFichaUseCase extends UseCase<AgregacionEstadoEvaluacionFichaDomain, UUID> {}
