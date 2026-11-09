package com.arquisoft.artefactos.application.paqueteentregable.command.usecase;

import com.arquisoft.artefactos.application.paqueteentregable.command.result.PurgaPaquetesAnterioresResult;
import com.arquisoft.artefactos.domain.paqueteentregable.PurgaPaquetesAnterioresDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface PurgarPaquetesAnterioresUseCase
        extends UseCase<PurgaPaquetesAnterioresDomain, PurgaPaquetesAnterioresResult> {
}
