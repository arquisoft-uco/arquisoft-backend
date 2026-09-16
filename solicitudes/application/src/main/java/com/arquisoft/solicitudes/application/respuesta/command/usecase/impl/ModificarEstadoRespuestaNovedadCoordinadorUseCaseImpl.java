package com.arquisoft.solicitudes.application.respuesta.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.RespuestaKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.application.respuesta.command.finder.DatosRespuestaFinder;
import com.arquisoft.solicitudes.application.respuesta.command.secondaryport.RespuestaOutputPort;
import com.arquisoft.solicitudes.application.respuesta.command.usecase.ModificarEstadoRespuestaNovedadCoordinadorUseCase;
import com.arquisoft.solicitudes.application.respuesta.command.validator.ModificarEstadoRespuestaNovedadCoordinadorValidator;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DatosSolicitudFinder;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DatosUsuarioFinder;
import com.arquisoft.solicitudes.domain.estadorespuesta.EstadoRespuesta;
import com.arquisoft.solicitudes.domain.respuesta.ModificacionEstadoRespuestaNovedadCoordinadorDomain;
import com.arquisoft.solicitudes.domain.respuesta.event.SolicitudNovedadCoordinadorEstadoModificadoEvent;
import com.arquisoft.solicitudes.domain.respuesta.model.ResumenRespuesta;
import com.arquisoft.solicitudes.domain.solicitud.model.ResumenSolicitud;
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
        var existeSolicitud = resumenSolicitud.isPresent();
        var tipoProyectado = resumenSolicitud.map(ResumenSolicitud::tipoSolicitud)
                .orElse(UtilTexto.VACIO);
        var destinatarioUsuarioProyectado = resumenSolicitud.map(ResumenSolicitud::destinatarioUsuario)
                .orElse(UtilUUID.obtenerUUIDPorDefecto());

        var resumenRespuesta = datosRespuestaFinder.obtener(entrada.getSolicitud());
        var existeRespuesta = resumenRespuesta.isPresent();
        var estadoActual = resumenRespuesta.map(ResumenRespuesta::estado).orElse(UtilTexto.VACIO);

        logger.debug(RespuestaKey.LOG_VERIFICACION_MODIFICACION_ESTADO, existeSolicitud, existeRespuesta);

        validator.validar(entrada, existeSolicitud, tipoProyectado,
                destinatarioUsuarioProyectado, existeRespuesta, estadoActual);

        respuestaOutputPort.actualizarEstadoPorSolicitud(entrada.getSolicitud(), entrada.getNuevoEstado());

        var datos = resumenSolicitud.orElseThrow();
        var remitente = datosUsuarioFinder.obtener(datos.remitenteUsuario()).orElseThrow();
        var coordinador = datosUsuarioFinder.obtener(datos.destinatarioUsuario()).orElseThrow();

        eventPublisher.publish(new SolicitudNovedadCoordinadorEstadoModificadoEvent(
                entrada.getSolicitud(), entrada.getNuevoEstado(),
                EstadoRespuesta.desde(entrada.getNuevoEstado()).getNombre(),
                remitente.getNombre(), remitente.getEmail(), coordinador.getNombre()));

        logger.info(RespuestaKey.LOG_ESTADO_MODIFICADO, entrada.getSolicitud(), entrada.getNuevoEstado());
    }
}
