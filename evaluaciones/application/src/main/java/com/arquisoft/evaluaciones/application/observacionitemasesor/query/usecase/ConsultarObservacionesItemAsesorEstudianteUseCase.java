package com.arquisoft.evaluaciones.application.observacionitemasesor.query.usecase;

import com.arquisoft.evaluaciones.application.observacionitemasesor.query.criteria.ObservacionItemAsesorCriteria;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.readmodel.ObservacionItemAsesorReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarObservacionesItemAsesorEstudianteUseCase
        extends UseCase<ObservacionItemAsesorCriteria, PaginatedResult<ObservacionItemAsesorReadModel>> {
}
