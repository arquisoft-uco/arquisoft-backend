package com.arquisoft.evaluaciones.application.evaluacionjurado.command.usecase;

import com.arquisoft.evaluaciones.domain.evaluacionjurado.AsignacionJuradosEvaluacionDomain;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;
import java.util.UUID;

public interface AsignarJuradosEvaluacionUseCase
        extends UseCase<AsignacionJuradosEvaluacionDomain, List<UUID>> {
}
