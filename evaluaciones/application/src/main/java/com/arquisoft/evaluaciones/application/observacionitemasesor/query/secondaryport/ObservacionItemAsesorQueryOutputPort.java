package com.arquisoft.evaluaciones.application.observacionitemasesor.query.secondaryport;

import com.arquisoft.evaluaciones.application.observacionitemasesor.query.criteria.ObservacionItemAsesorCriteria;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.readmodel.ObservacionItemAsesorReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ObservacionItemAsesorQueryOutputPort {

    PaginatedResult<ObservacionItemAsesorReadModel> consultarTodas(ObservacionItemAsesorCriteria criteria);
}
