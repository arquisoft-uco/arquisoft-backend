package com.arquisoft.solicitudes.application.solicitud.query.primaryport.interactor;

import com.arquisoft.solicitudes.application.solicitud.query.primaryport.model.ConsultarSolicitudesRegistroModificacionUsuariosRecibidasQuery;
import com.arquisoft.solicitudes.application.solicitud.query.readmodel.SolicitudReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarSolicitudesRegistroModificacionUsuariosRecibidasInteractor
        extends Interactor<ConsultarSolicitudesRegistroModificacionUsuariosRecibidasQuery,
        PaginatedResult<SolicitudReadModel>> {}
