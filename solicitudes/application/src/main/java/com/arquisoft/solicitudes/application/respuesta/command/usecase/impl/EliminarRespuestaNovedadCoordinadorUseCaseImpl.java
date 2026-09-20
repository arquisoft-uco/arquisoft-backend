package com.arquisoft.solicitudes.application.respuesta.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.RespuestaKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.solicitudes.application.respuesta.command.finder.DatosRespuestaFinder;
import com.arquisoft.solicitudes.application.respuesta.command.secondaryport.RespuestaOutputPort;
import com.arquisoft.solicitudes.application.respuesta.command.usecase.EliminarRespuestaNovedadCoordinadorUseCase;
import com.arquisoft.solicitudes.application.respuesta.command.validator.EliminarRespuestaNovedadCoordinadorValidator;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DatosSolicitudFinder;
import com.arquisoft.solicitudes.domain.respuesta.EliminacionRespuestaNovedadCoordinadorDomain;
import com.arquisoft.solicitudes.domain.respuesta.event.SolicitudNovedadCoordinadorRespuestaEliminadaEvent;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EliminarRespuestaNovedadCoordinadorUseCaseImpl
        implements EliminarRespuestaNovedadCoordinadorUseCase {

    private final DatosSolicitudFinder datosSolicitudFinder;
    private final DatosRespuestaFinder datosRespuestaFinder;
    private final RespuestaOutputPort respuestaOutputPort;
    private final EliminarRespuestaNovedadCoordinadorValidator validator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(EliminacionRespuestaNovedadCoordinadorDomain entrada) {
        logger.info(RespuestaKey.LOG_ELIMINANDO,
                entrada.getSolicitud(), entrada.getCoordinadorUsuario());

        var resumenSolicitud = datosSolicitudFinder.obtener(entrada.getSolicitud());
        var existeSolicitud = !resumenSolicitud.esVacio();

        var resumenRespuesta = datosRespuestaFinder.obtener(entrada.getSolicitud());
        var existeRespuesta = !resumenRespuesta.esVacio();

        logger.debug(RespuestaKey.LOG_VERIFICACION_ELIMINACION, existeSolicitud, existeRespuesta);

        validator.validar(entrada.getSolicitud(), existeSolicitud, resumenSolicitud.tipoSolicitud(),
                resumenSolicitud.destinatarioUsuario(), entrada.getCoordinadorUsuario(),
                existeRespuesta, resumenRespuesta.estado());

        respuestaOutputPort.eliminarPorSolicitud(entrada.getSolicitud());

        eventPublisher.publish(new SolicitudNovedadCoordinadorRespuestaEliminadaEvent(
                entrada.getSolicitud(), entrada.getCoordinadorUsuario(),
                TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId()));

        logger.info(RespuestaKey.LOG_ELIMINADA, entrada.getSolicitud());
    }
}
