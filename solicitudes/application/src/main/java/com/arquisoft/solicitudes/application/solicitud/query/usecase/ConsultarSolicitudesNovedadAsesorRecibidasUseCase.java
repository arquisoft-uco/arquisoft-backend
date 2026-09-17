package com.arquisoft.solicitudes.application.solicitud.query.usecase;

import com.arquisoft.solicitudes.application.solicitud.query.criteria.SolicitudCriteria;
import com.arquisoft.solicitudes.application.solicitud.query.readmodel.SolicitudReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarSolicitudesNovedadAsesorRecibidasUseCase
        extends UseCase<SolicitudCriteria, PaginatedResult<SolicitudReadModel>> {}
