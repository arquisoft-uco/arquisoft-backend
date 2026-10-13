package com.arquisoft.solicitudes.application.respuesta.query.secondaryport;

import com.arquisoft.solicitudes.application.respuesta.query.criteria.RespuestaCriteria;
import com.arquisoft.solicitudes.application.respuesta.query.readmodel.RespuestaReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface RespuestaQueryOutputPort {

    PaginatedResult<RespuestaReadModel> consultar(RespuestaCriteria criteria);
}
