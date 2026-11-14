package com.arquisoft.evaluaciones.application.observacionitemasesor.query.primaryport.interactor;

import com.arquisoft.evaluaciones.application.observacionitemasesor.query.primaryport.model.ConsultarObservacionesItemAsesorEstudianteQuery;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.readmodel.ObservacionItemAsesorReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarObservacionesItemAsesorEstudianteInteractor extends
        Interactor<ConsultarObservacionesItemAsesorEstudianteQuery, PaginatedResult<ObservacionItemAsesorReadModel>> {
}
