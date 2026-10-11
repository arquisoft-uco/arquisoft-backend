package com.arquisoft.solicitudes.application.respuesta.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.RespuestaKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.solicitudes.application.respuesta.command.secondaryport.RespuestaOutputPort;
import com.arquisoft.solicitudes.application.respuesta.command.secondaryport.mapper.RespuestaMapper;
import com.arquisoft.solicitudes.application.respuesta.command.usecase.ResponderSolicitudUseCase;
import com.arquisoft.solicitudes.application.respuesta.command.validator.ResponderSolicitudValidator;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DatosSolicitudFinder;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DatosUsuarioFinder;
import com.arquisoft.solicitudes.application.solicitud.command.finder.SolicitudTieneRespuestasFinder;
import com.arquisoft.solicitudes.domain.respuesta.RespuestaSolicitudDomain;
import com.arquisoft.solicitudes.domain.respuesta.event.SolicitudRespondidaEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ResponderSolicitudUseCaseImpl implements ResponderSolicitudUseCase {

    private final DatosSolicitudFinder datosSolicitudFinder;
    private final DatosUsuarioFinder datosUsuarioFinder;
    private final SolicitudTieneRespuestasFinder solicitudTieneRespuestasFinder;
    private final ResponderSolicitudValidator validator;
    private final RespuestaOutputPort respuestaOutputPort;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(RespuestaSolicitudDomain entrada) {
        var tipo = entrada.getTipoEsperado().getId();
        logger.info(RespuestaKey.LOG_RESPONDIENDO,
                tipo, entrada.getSolicitud(), entrada.getResponsableUsuario());

        var resumen = datosSolicitudFinder.obtener(entrada.getSolicitud());
        var yaRespondida = solicitudTieneRespuestasFinder.obtener(entrada.getSolicitud());
        var remitente = datosUsuarioFinder.obtener(resumen.remitenteUsuario());
        var responsable = datosUsuarioFinder.obtener(resumen.destinatarioUsuario());

        logger.debug(RespuestaKey.LOG_VERIFICACION_RESPUESTA,
                !resumen.esVacio(), yaRespondida, !remitente.esVacio(), !responsable.esVacio());

        validator.validar(entrada, resumen, remitente, responsable, yaRespondida);

        var respuesta = entrada.getRespuesta();
        respuestaOutputPort.registrar(RespuestaMapper.toEntity(respuesta));

        eventPublisher.publish(new SolicitudRespondidaEvent(
                entrada.getTipoEsperado(), respuesta, remitente, responsable));

        logger.info(RespuestaKey.LOG_RESPONDIDA, tipo, respuesta.getId());
        return respuesta.getId();
    }
}
