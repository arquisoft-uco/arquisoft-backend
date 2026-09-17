package com.arquisoft.solicitudes.application.solicitud.query.secondaryport;

import com.arquisoft.solicitudes.application.solicitud.query.criteria.SolicitudCriteria;
import com.arquisoft.solicitudes.application.solicitud.query.readmodel.SolicitudReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface SolicitudQueryOutputPort {

    PaginatedResult<SolicitudReadModel> consultar(SolicitudCriteria criteria);
}
