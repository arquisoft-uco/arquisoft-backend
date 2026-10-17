package com.arquisoft.fichas.application.observacionitem.query.primaryport.interactor;

import com.arquisoft.fichas.application.observacionitem.query.primaryport.model.ConsultarObservacionesItemEstudianteQuery;
import com.arquisoft.fichas.application.observacionitem.query.readmodel.ObservacionItemReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarObservacionesItemEstudianteInteractor
        extends Interactor<ConsultarObservacionesItemEstudianteQuery, PaginatedResult<ObservacionItemReadModel>> {}
