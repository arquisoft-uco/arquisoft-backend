package com.arquisoft.solicitudes.application.respuesta.query.usecase;

import com.arquisoft.solicitudes.application.respuesta.query.criteria.RespuestaCriteria;
import com.arquisoft.solicitudes.application.respuesta.query.readmodel.RespuestaReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarRespuestasNovedadCoordinadorEnviadasUseCase
        extends UseCase<RespuestaCriteria, PaginatedResult<RespuestaReadModel>> {}
