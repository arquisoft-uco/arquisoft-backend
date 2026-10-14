package com.arquisoft.solicitudes.application.solicitud.query.primaryport.interactor;

import com.arquisoft.solicitudes.application.solicitud.query.primaryport.model.ConsultarSolicitudesCambioAsesorEnviadasQuery;
import com.arquisoft.solicitudes.application.solicitud.query.readmodel.SolicitudReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarSolicitudesCambioAsesorEnviadasInteractor
        extends Interactor<ConsultarSolicitudesCambioAsesorEnviadasQuery,
        PaginatedResult<SolicitudReadModel>> {}
