package com.arquisoft.fichas.application.representantecomite.command.usecase;

import com.arquisoft.fichas.application.representantecomite.command.result.AgregacionRepresentanteComiteResult;
import com.arquisoft.fichas.domain.representantecomite.RepresentanteComiteDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface AgregarRepresentanteComiteUseCase
        extends UseCase<RepresentanteComiteDomain, AgregacionRepresentanteComiteResult> {
}
