package com.arquisoft.solicitudes.application.respuesta.query.primaryport.interactor;

import com.arquisoft.solicitudes.application.respuesta.query.primaryport.model.ConsultarRespuestasNovedadAsesorRecibidasQuery;
import com.arquisoft.solicitudes.application.respuesta.query.readmodel.RespuestaReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarRespuestasNovedadAsesorRecibidasInteractor
        extends Interactor<ConsultarRespuestasNovedadAsesorRecibidasQuery,
        PaginatedResult<RespuestaReadModel>> {}
