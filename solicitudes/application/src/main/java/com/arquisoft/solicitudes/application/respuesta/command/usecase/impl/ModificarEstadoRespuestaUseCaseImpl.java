package com.arquisoft.solicitudes.application.respuesta.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.RespuestaKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.solicitudes.application.respuesta.command.finder.DatosRespuestaFinder;
import com.arquisoft.solicitudes.application.respuesta.command.secondaryport.RespuestaOutputPort;
import com.arquisoft.solicitudes.application.respuesta.command.usecase.ModificarEstadoRespuestaUseCase;
import com.arquisoft.solicitudes.application.respuesta.command.validator.ModificarEstadoRespuestaValidator;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DatosSolicitudFinder;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DatosUsuarioFinder;
import com.arquisoft.solicitudes.domain.respuesta.ModificacionEstadoRespuestaDomain;
import com.arquisoft.solicitudes.domain.respuesta.event.SolicitudEstadoModificadoEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModificarEstadoRespuestaUseCaseImpl
        implements ModificarEstadoRespuestaUseCase {

    private final DatosSolicitudFinder datosSolicitudFinder;
    private final DatosRespuestaFinder datosRespuestaFinder;
    private final DatosUsuarioFinder datosUsuarioFinder;
    private final RespuestaOutputPort respuestaOutputPort;
    private final ModificarEstadoRespuestaValidator validator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(ModificacionEstadoRespuestaDomain entrada) {
        var tipo = entrada.getTipoEsperado().getId();
        logger.info(RespuestaKey.LOG_MODIFICANDO_ESTADO,
                tipo, entrada.getSolicitud(), entrada.getResponsableUsuario());

        var resumenSolicitud = datosSolicitudFinder.obtener(entrada.getSolicitud());
        var resumenRespuesta = datosRespuestaFinder.obtener(entrada.getSolicitud());
        var remitente = datosUsuarioFinder.obtener(resumenSolicitud.remitenteUsuario());
        var responsable = datosUsuarioFinder.obtener(resumenSolicitud.destinatarioUsuario());

        logger.debug(RespuestaKey.LOG_VERIFICACION_MODIFICACION_ESTADO,
                !resumenSolicitud.esVacio(), !resumenRespuesta.esVacio(),
                !remitente.esVacio(), !responsable.esVacio());

        validator.validar(entrada, resumenSolicitud, resumenRespuesta, remitente, responsable);

        var nuevoEstado = entrada.getNuevoEstado();
        respuestaOutputPort.actualizarEstadoPorSolicitud(entrada.getSolicitud(), nuevoEstado.getId());

        eventPublisher.publish(new SolicitudEstadoModificadoEvent(
                entrada.getTipoEsperado(), entrada.getSolicitud(), nuevoEstado, remitente, responsable));

        logger.info(RespuestaKey.LOG_ESTADO_MODIFICADO, tipo, entrada.getSolicitud(), nuevoEstado.getId());
    }
}
