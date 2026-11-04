package com.arquisoft.evaluaciones.application.observacionitemjurado.query.usecase;

import com.arquisoft.evaluaciones.application.observacionitemjurado.query.criteria.ObservacionItemJuradoCriteria;
import com.arquisoft.evaluaciones.application.observacionitemjurado.query.readmodel.ObservacionItemJuradoReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarObservacionesItemJuradoUseCase
        extends UseCase<ObservacionItemJuradoCriteria, PaginatedResult<ObservacionItemJuradoReadModel>> {
}
