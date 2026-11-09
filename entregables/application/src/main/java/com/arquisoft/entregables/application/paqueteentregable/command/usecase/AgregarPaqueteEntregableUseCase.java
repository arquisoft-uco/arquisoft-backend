package com.arquisoft.entregables.application.paqueteentregable.command.usecase;

import com.arquisoft.entregables.application.paqueteentregable.command.result.AgregacionPaqueteEntregableResult;
import com.arquisoft.entregables.domain.paqueteentregable.PaqueteEntregableDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface AgregarPaqueteEntregableUseCase
        extends UseCase<PaqueteEntregableDomain, AgregacionPaqueteEntregableResult> {
}
