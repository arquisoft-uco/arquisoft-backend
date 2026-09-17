package com.arquisoft.solicitudes.application.solicitud.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.SolicitudKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DatosSolicitudFinder;
import com.arquisoft.solicitudes.application.solicitud.command.finder.SolicitudTieneRespuestasFinder;
import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.SolicitudOutputPort;
import com.arquisoft.solicitudes.application.solicitud.command.usecase.EliminarSolicitudNovedadCoordinadorUseCase;
import com.arquisoft.solicitudes.application.solicitud.command.validator.EliminarSolicitudNovedadCoordinadorValidator;
import com.arquisoft.solicitudes.domain.solicitud.EliminacionSolicitudNovedadCoordinadorDomain;
import com.arquisoft.solicitudes.domain.solicitud.event.SolicitudNovedadCoordinadorEliminadaEvent;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EliminarSolicitudNovedadCoordinadorUseCaseImpl
        implements EliminarSolicitudNovedadCoordinadorUseCase {

    private final SolicitudOutputPort solicitudOutputPort;
    private final DatosSolicitudFinder datosSolicitudFinder;
    private final SolicitudTieneRespuestasFinder solicitudTieneRespuestasFinder;
    private final EliminarSolicitudNovedadCoordinadorValidator validator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(EliminacionSolicitudNovedadCoordinadorDomain entrada) {
        logger.info(SolicitudKey.LOG_ELIMINANDO, entrada.getSolicitud(), entrada.getRemitenteUsuario());

        var resumen = datosSolicitudFinder.obtener(entrada.getSolicitud());
        var existe = !resumen.esVacio();
        var remitenteUsuarioProyectado = resumen.remitenteUsuario();
        var tipoProyectado = resumen.tipoSolicitud();
        var tieneRespuestas = solicitudTieneRespuestasFinder.obtener(entrada.getSolicitud());

        logger.debug(SolicitudKey.LOG_VERIFICACION_ELIMINACION, existe, tieneRespuestas);

        validator.validar(entrada.getSolicitud(), existe, remitenteUsuarioProyectado, tipoProyectado,
                entrada.getRemitenteUsuario(), tieneRespuestas);

        solicitudOutputPort.eliminar(entrada.getSolicitud());

        eventPublisher.publish(new SolicitudNovedadCoordinadorEliminadaEvent(
                entrada.getSolicitud(), entrada.getRemitenteUsuario(),
                TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId()));

        logger.info(SolicitudKey.LOG_ELIMINADA, entrada.getSolicitud());
    }
}
