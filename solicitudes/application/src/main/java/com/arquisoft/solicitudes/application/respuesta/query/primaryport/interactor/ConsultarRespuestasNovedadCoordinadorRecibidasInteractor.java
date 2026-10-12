package com.arquisoft.solicitudes.application.respuesta.query.primaryport.interactor;

import com.arquisoft.solicitudes.application.respuesta.query.primaryport.model.ConsultarRespuestasNovedadCoordinadorRecibidasQuery;
import com.arquisoft.solicitudes.application.respuesta.query.readmodel.RespuestaReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarRespuestasNovedadCoordinadorRecibidasInteractor
        extends Interactor<ConsultarRespuestasNovedadCoordinadorRecibidasQuery,
        PaginatedResult<RespuestaReadModel>> {}
