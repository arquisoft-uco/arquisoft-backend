package com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ResponderSolicitudAmpliacionPlazoCommand;
import com.arquisoft.solicitudes.domain.respuesta.RespuestaDomain;
import com.arquisoft.solicitudes.domain.respuesta.RespuestaSolicitudDomain;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;

public final class ResponderSolicitudAmpliacionPlazoMapper {

    private ResponderSolicitudAmpliacionPlazoMapper() {}

    public static RespuestaSolicitudDomain toDomain(
            ResponderSolicitudAmpliacionPlazoCommand command) {
        var respuesta = RespuestaDomain.crear(command.solicitud(), command.contenido());
        return RespuestaSolicitudDomain.crear(
                respuesta, command.coordinadorUsuario(), TipoSolicitud.AMPLIACION_DE_PLAZO);
    }
}
