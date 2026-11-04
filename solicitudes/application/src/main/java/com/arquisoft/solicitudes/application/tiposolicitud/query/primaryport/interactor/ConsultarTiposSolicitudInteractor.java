package com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.interactor;

import com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.model.ConsultarTiposSolicitudQuery;
import com.arquisoft.solicitudes.application.tiposolicitud.query.readmodel.TipoSolicitudReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarTiposSolicitudInteractor
        extends Interactor<ConsultarTiposSolicitudQuery, List<TipoSolicitudReadModel>> {
}
