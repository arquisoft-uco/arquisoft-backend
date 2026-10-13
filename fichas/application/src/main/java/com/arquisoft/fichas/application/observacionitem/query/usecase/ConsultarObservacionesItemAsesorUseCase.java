package com.arquisoft.fichas.application.observacionitem.query.usecase;

import com.arquisoft.fichas.application.observacionitem.query.criteria.ObservacionItemCriteria;
import com.arquisoft.fichas.application.observacionitem.query.readmodel.ObservacionItemReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarObservacionesItemAsesorUseCase
        extends UseCase<ObservacionItemCriteria, PaginatedResult<ObservacionItemReadModel>> {}
