package com.arquisoft.evaluaciones.application.jurado.command.usecase;

import com.arquisoft.evaluaciones.application.jurado.command.result.AgregacionJuradoResult;
import com.arquisoft.evaluaciones.domain.jurado.JuradoDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface AgregarJuradoUseCase extends UseCase<JuradoDomain, AgregacionJuradoResult> {
}
