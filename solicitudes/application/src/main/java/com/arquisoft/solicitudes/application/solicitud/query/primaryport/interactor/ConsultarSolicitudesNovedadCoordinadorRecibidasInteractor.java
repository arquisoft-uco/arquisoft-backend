package com.arquisoft.solicitudes.application.solicitud.query.primaryport.interactor;

import com.arquisoft.solicitudes.application.solicitud.query.primaryport.model.ConsultarSolicitudesNovedadCoordinadorRecibidasQuery;
import com.arquisoft.solicitudes.application.solicitud.query.readmodel.SolicitudReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarSolicitudesNovedadCoordinadorRecibidasInteractor
        extends Interactor<ConsultarSolicitudesNovedadCoordinadorRecibidasQuery,
        PaginatedResult<SolicitudReadModel>> {}
