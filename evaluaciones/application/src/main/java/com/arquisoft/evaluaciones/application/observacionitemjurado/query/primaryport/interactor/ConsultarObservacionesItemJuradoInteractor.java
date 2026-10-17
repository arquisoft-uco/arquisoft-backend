package com.arquisoft.evaluaciones.application.observacionitemjurado.query.primaryport.interactor;

import com.arquisoft.evaluaciones.application.observacionitemjurado.query.primaryport.model.ConsultarObservacionesItemJuradoQuery;
import com.arquisoft.evaluaciones.application.observacionitemjurado.query.readmodel.ObservacionItemJuradoReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarObservacionesItemJuradoInteractor
        extends Interactor<ConsultarObservacionesItemJuradoQuery, PaginatedResult<ObservacionItemJuradoReadModel>> {
}
