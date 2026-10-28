package com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ModificarEstadoRespuestaAmpliacionPlazoCommand;
import com.arquisoft.solicitudes.domain.respuesta.ModificacionEstadoRespuestaDomain;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;

public final class ModificarEstadoRespuestaAmpliacionPlazoMapper {

    private ModificarEstadoRespuestaAmpliacionPlazoMapper() {}

    public static ModificacionEstadoRespuestaDomain toDomain(
            ModificarEstadoRespuestaAmpliacionPlazoCommand command) {
        return ModificacionEstadoRespuestaDomain.crear(
                command.solicitud(), command.coordinadorUsuario(), command.nuevoEstado(),
                TipoSolicitud.AMPLIACION_DE_PLAZO);
    }
}
