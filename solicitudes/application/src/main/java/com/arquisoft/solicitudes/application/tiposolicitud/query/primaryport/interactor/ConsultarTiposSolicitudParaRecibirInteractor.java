package com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.interactor;

import com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.model.ConsultarTiposSolicitudParaRecibirQuery;
import com.arquisoft.solicitudes.application.tiposolicitud.query.readmodel.TipoSolicitudReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarTiposSolicitudParaRecibirInteractor
        extends Interactor<ConsultarTiposSolicitudParaRecibirQuery, List<TipoSolicitudReadModel>> {
}
