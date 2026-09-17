package com.arquisoft.solicitudes.application.solicitud.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.SolicitudKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DatosSolicitudFinder;
import com.arquisoft.solicitudes.application.solicitud.command.finder.SolicitudTieneRespuestasFinder;
import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.SolicitudOutputPort;
import com.arquisoft.solicitudes.application.solicitud.command.usecase.EliminarSolicitudNovedadAsesorUseCase;
import com.arquisoft.solicitudes.application.solicitud.command.validator.EliminarSolicitudNovedadAsesorValidator;
import com.arquisoft.solicitudes.domain.solicitud.EliminacionSolicitudNovedadAsesorDomain;
import com.arquisoft.solicitudes.domain.solicitud.event.SolicitudNovedadAsesorEliminadaEvent;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EliminarSolicitudNovedadAsesorUseCaseImpl
        implements EliminarSolicitudNovedadAsesorUseCase {

    private final SolicitudOutputPort solicitudOutputPort;
    private final DatosSolicitudFinder datosSolicitudFinder;
    private final SolicitudTieneRespuestasFinder solicitudTieneRespuestasFinder;
    private final EliminarSolicitudNovedadAsesorValidator validator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(EliminacionSolicitudNovedadAsesorDomain entrada) {
        logger.info(SolicitudKey.LOG_ELIMINANDO_ASESOR, entrada.getSolicitud(), entrada.getRemitenteUsuario());

        var resumen = datosSolicitudFinder.obtener(entrada.getSolicitud());
        var existe = !resumen.esVacio();
        var remitenteUsuarioProyectado = resumen.remitenteUsuario();
        var tipoProyectado = resumen.tipoSolicitud();
        var tieneRespuestas = solicitudTieneRespuestasFinder.obtener(entrada.getSolicitud());

        logger.debug(SolicitudKey.LOG_VERIFICACION_ELIMINACION, existe, tieneRespuestas);

        validator.validar(entrada.getSolicitud(), existe, remitenteUsuarioProyectado, tipoProyectado,
                entrada.getRemitenteUsuario(), tieneRespuestas);

        solicitudOutputPort.eliminar(entrada.getSolicitud());

        eventPublisher.publish(new SolicitudNovedadAsesorEliminadaEvent(
                entrada.getSolicitud(), entrada.getRemitenteUsuario(),
                TipoSolicitud.NOVEDAD_PARA_EL_ASESOR.getId()));

        logger.info(SolicitudKey.LOG_ELIMINADA_ASESOR, entrada.getSolicitud());
    }
}
