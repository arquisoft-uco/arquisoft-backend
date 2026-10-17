package com.arquisoft.solicitudes.application.solicitud.query.primaryport.interactor;

import com.arquisoft.solicitudes.application.solicitud.query.primaryport.model.ConsultarSolicitudesCambioAsesorRecibidasQuery;
import com.arquisoft.solicitudes.application.solicitud.query.readmodel.SolicitudReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarSolicitudesCambioAsesorRecibidasInteractor
        extends Interactor<ConsultarSolicitudesCambioAsesorRecibidasQuery,
        PaginatedResult<SolicitudReadModel>> {}
