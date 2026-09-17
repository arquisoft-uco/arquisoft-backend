package com.arquisoft.solicitudes.application.respuesta.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.RespuestaKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.solicitudes.application.respuesta.command.finder.DatosRespuestaFinder;
import com.arquisoft.solicitudes.application.respuesta.command.secondaryport.RespuestaOutputPort;
import com.arquisoft.solicitudes.application.respuesta.command.usecase.ModificarEstadoRespuestaNovedadCoordinadorUseCase;
import com.arquisoft.solicitudes.application.respuesta.command.validator.ModificarEstadoRespuestaNovedadCoordinadorValidator;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DatosSolicitudFinder;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DatosUsuarioFinder;
import com.arquisoft.solicitudes.domain.estadorespuesta.EstadoRespuesta;
import com.arquisoft.solicitudes.domain.respuesta.ModificacionEstadoRespuestaNovedadCoordinadorDomain;
import com.arquisoft.solicitudes.domain.respuesta.event.SolicitudNovedadCoordinadorEstadoModificadoEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModificarEstadoRespuestaNovedadCoordinadorUseCaseImpl
        implements ModificarEstadoRespuestaNovedadCoordinadorUseCase {

    private final DatosSolicitudFinder datosSolicitudFinder;
    private final DatosRespuestaFinder datosRespuestaFinder;
    private final DatosUsuarioFinder datosUsuarioFinder;
    private final RespuestaOutputPort respuestaOutputPort;
    private final ModificarEstadoRespuestaNovedadCoordinadorValidator validator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(ModificacionEstadoRespuestaNovedadCoordinadorDomain entrada) {
        logger.info(RespuestaKey.LOG_MODIFICANDO_ESTADO,
                entrada.getSolicitud(), entrada.getCoordinadorUsuario());

        var resumenSolicitud = datosSolicitudFinder.obtener(entrada.getSolicitud());
        var existeSolicitud = !resumenSolicitud.esVacio();

        var resumenRespuesta = datosRespuestaFinder.obtener(entrada.getSolicitud());
        var existeRespuesta = !resumenRespuesta.esVacio();

        logger.debug(RespuestaKey.LOG_VERIFICACION_MODIFICACION_ESTADO, existeSolicitud, existeRespuesta);

        validator.validar(entrada, existeSolicitud, resumenSolicitud.tipoSolicitud(),
                resumenSolicitud.destinatarioUsuario(), existeRespuesta, resumenRespuesta.estado());

        respuestaOutputPort.actualizarEstadoPorSolicitud(entrada.getSolicitud(), entrada.getNuevoEstado());

        var remitente = datosUsuarioFinder.obtener(resumenSolicitud.remitenteUsuario());
        var coordinador = datosUsuarioFinder.obtener(resumenSolicitud.destinatarioUsuario());

        eventPublisher.publish(new SolicitudNovedadCoordinadorEstadoModificadoEvent(
                entrada.getSolicitud(), entrada.getNuevoEstado(),
                EstadoRespuesta.desde(entrada.getNuevoEstado()).getNombre(),
                remitente.getNombre(), remitente.getEmail(), coordinador.getNombre()));

        logger.info(RespuestaKey.LOG_ESTADO_MODIFICADO, entrada.getSolicitud(), entrada.getNuevoEstado());
    }
}
