package com.arquisoft.fichas.application.representantecomite.command.usecase;

import com.arquisoft.fichas.application.representantecomite.command.result.ActualizacionRepresentanteComiteResult;
import com.arquisoft.fichas.domain.representantecomite.RepresentanteComiteDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface ActualizarRepresentanteComiteUseCase
        extends UseCase<RepresentanteComiteDomain, ActualizacionRepresentanteComiteResult> {
}
