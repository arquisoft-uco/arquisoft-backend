package com.arquisoft.evaluaciones.application.jurado.command.usecase;

import com.arquisoft.evaluaciones.application.jurado.command.result.RemocionJuradoResult;
import com.arquisoft.evaluaciones.domain.jurado.JuradoDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface RemoverJuradoUseCase extends UseCase<JuradoDomain, RemocionJuradoResult> {
}
