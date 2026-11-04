package com.arquisoft.evaluaciones.application.observacionitemjurado.query.secondaryport;

import com.arquisoft.evaluaciones.application.observacionitemjurado.query.criteria.ObservacionItemJuradoCriteria;
import com.arquisoft.evaluaciones.application.observacionitemjurado.query.readmodel.ObservacionItemJuradoReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ObservacionItemJuradoQueryOutputPort {

    PaginatedResult<ObservacionItemJuradoReadModel> consultarTodas(ObservacionItemJuradoCriteria criteria);
}
