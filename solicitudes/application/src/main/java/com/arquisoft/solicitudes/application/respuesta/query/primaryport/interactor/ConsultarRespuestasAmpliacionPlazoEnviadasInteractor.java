package com.arquisoft.solicitudes.application.respuesta.query.primaryport.interactor;

import com.arquisoft.solicitudes.application.respuesta.query.primaryport.model.ConsultarRespuestasAmpliacionPlazoEnviadasQuery;
import com.arquisoft.solicitudes.application.respuesta.query.readmodel.RespuestaReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarRespuestasAmpliacionPlazoEnviadasInteractor
        extends Interactor<ConsultarRespuestasAmpliacionPlazoEnviadasQuery,
        PaginatedResult<RespuestaReadModel>> {}
