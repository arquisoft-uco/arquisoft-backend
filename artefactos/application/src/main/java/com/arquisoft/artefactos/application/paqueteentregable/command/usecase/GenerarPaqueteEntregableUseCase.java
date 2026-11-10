package com.arquisoft.artefactos.application.paqueteentregable.command.usecase;

import com.arquisoft.artefactos.application.paqueteentregable.command.result.GeneracionPaqueteEntregableResult;
import com.arquisoft.artefactos.domain.paqueteentregable.GeneracionPaqueteEntregableDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface GenerarPaqueteEntregableUseCase
        extends UseCase<GeneracionPaqueteEntregableDomain, GeneracionPaqueteEntregableResult> {
}
