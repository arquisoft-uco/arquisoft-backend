package com.arquisoft.solicitudes.application.tiposolicitud.query.secondaryport;

import com.arquisoft.solicitudes.application.tiposolicitud.query.readmodel.TipoSolicitudReadModel;

import java.util.List;
import java.util.Set;

public interface TipoSolicitudQueryOutputPort {

    List<TipoSolicitudReadModel> consultarPorIds(Set<String> ids);
}
