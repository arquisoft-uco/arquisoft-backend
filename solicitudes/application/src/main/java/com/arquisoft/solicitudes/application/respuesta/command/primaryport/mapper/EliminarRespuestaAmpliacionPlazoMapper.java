package com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.EliminarRespuestaAmpliacionPlazoCommand;
import com.arquisoft.solicitudes.domain.respuesta.EliminacionRespuestaDomain;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;

public final class EliminarRespuestaAmpliacionPlazoMapper {

    private EliminarRespuestaAmpliacionPlazoMapper() {}

    public static EliminacionRespuestaDomain toDomain(
            EliminarRespuestaAmpliacionPlazoCommand command) {
        return EliminacionRespuestaDomain.crear(
                command.solicitud(), command.coordinadorUsuario(), TipoSolicitud.AMPLIACION_DE_PLAZO);
    }
}
