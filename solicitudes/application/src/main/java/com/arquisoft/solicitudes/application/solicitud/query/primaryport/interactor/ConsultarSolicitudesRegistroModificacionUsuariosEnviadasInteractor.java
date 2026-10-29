package com.arquisoft.solicitudes.application.solicitud.query.primaryport.interactor;

import com.arquisoft.solicitudes.application.solicitud.query.primaryport.model.ConsultarSolicitudesRegistroModificacionUsuariosEnviadasQuery;
import com.arquisoft.solicitudes.application.solicitud.query.readmodel.SolicitudReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarSolicitudesRegistroModificacionUsuariosEnviadasInteractor
        extends Interactor<ConsultarSolicitudesRegistroModificacionUsuariosEnviadasQuery,
        PaginatedResult<SolicitudReadModel>> {}
