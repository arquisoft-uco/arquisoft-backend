package com.arquisoft.fichas.application.observacionitem.query.usecase;

import com.arquisoft.fichas.application.observacionitem.query.criteria.ObservacionItemEstudianteCriteria;
import com.arquisoft.fichas.application.observacionitem.query.readmodel.ObservacionItemReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarObservacionesItemEstudianteUseCase
        extends UseCase<ObservacionItemEstudianteCriteria, PaginatedResult<ObservacionItemReadModel>> {}
