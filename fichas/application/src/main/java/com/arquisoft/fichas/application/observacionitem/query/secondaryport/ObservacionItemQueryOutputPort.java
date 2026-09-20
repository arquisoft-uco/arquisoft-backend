package com.arquisoft.fichas.application.observacionitem.query.secondaryport;

import com.arquisoft.fichas.application.observacionitem.query.criteria.ObservacionItemCriteria;
import com.arquisoft.fichas.application.observacionitem.query.readmodel.ObservacionItemReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ObservacionItemQueryOutputPort {

    PaginatedResult<ObservacionItemReadModel> consultarTodas(ObservacionItemCriteria criteria);
}
