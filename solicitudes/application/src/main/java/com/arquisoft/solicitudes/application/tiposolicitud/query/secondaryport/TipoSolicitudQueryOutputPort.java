package com.arquisoft.solicitudes.application.tiposolicitud.query.secondaryport;

import com.arquisoft.solicitudes.application.tiposolicitud.query.readmodel.TipoSolicitudReadModel;

import java.util.List;

public interface TipoSolicitudQueryOutputPort {

    List<TipoSolicitudReadModel> findAll();
}
