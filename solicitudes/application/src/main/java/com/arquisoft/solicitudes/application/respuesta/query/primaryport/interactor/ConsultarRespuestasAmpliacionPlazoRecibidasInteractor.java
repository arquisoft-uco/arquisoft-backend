package com.arquisoft.solicitudes.application.respuesta.query.primaryport.interactor;

import com.arquisoft.solicitudes.application.respuesta.query.primaryport.model.ConsultarRespuestasAmpliacionPlazoRecibidasQuery;
import com.arquisoft.solicitudes.application.respuesta.query.readmodel.RespuestaReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarRespuestasAmpliacionPlazoRecibidasInteractor
        extends Interactor<ConsultarRespuestasAmpliacionPlazoRecibidasQuery,
        PaginatedResult<RespuestaReadModel>> {}
