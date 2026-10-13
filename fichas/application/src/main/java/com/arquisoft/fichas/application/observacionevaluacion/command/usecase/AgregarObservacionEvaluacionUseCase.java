package com.arquisoft.fichas.application.observacionevaluacion.command.usecase;

import com.arquisoft.fichas.domain.observacionevaluacion.AgregacionObservacionEvaluacionDomain;
import com.arquisoft.shared.usecase.UseCase;

import java.util.UUID;

public interface AgregarObservacionEvaluacionUseCase extends UseCase<AgregacionObservacionEvaluacionDomain, UUID> {}
