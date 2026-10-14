package com.arquisoft.solicitudes.application.solicitud.query.primaryport.interactor;

import com.arquisoft.solicitudes.application.solicitud.query.primaryport.model.ConsultarSolicitudesAmpliacionPlazoEnviadasQuery;
import com.arquisoft.solicitudes.application.solicitud.query.readmodel.SolicitudReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarSolicitudesAmpliacionPlazoEnviadasInteractor
        extends Interactor<ConsultarSolicitudesAmpliacionPlazoEnviadasQuery,
        PaginatedResult<SolicitudReadModel>> {}
