package com.arquisoft.solicitudes.application.respuesta.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.RespuestaKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.solicitudes.application.respuesta.command.secondaryport.RespuestaOutputPort;
import com.arquisoft.solicitudes.application.respuesta.command.secondaryport.mapper.RespuestaMapper;
import com.arquisoft.solicitudes.application.respuesta.command.usecase.ResponderSolicitudNovedadCoordinadorUseCase;
import com.arquisoft.solicitudes.application.respuesta.command.validator.ResponderSolicitudNovedadCoordinadorValidator;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DatosSolicitudFinder;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DatosUsuarioFinder;
import com.arquisoft.solicitudes.application.solicitud.command.finder.SolicitudTieneRespuestasFinder;
import com.arquisoft.solicitudes.domain.respuesta.RespuestaDomain;
import com.arquisoft.solicitudes.domain.respuesta.RespuestaNovedadCoordinadorDomain;
import com.arquisoft.solicitudes.domain.respuesta.event.SolicitudNovedadCoordinadorRespondidaEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ResponderSolicitudNovedadCoordinadorUseCaseImpl
        implements ResponderSolicitudNovedadCoordinadorUseCase {

    private final DatosSolicitudFinder datosSolicitudFinder;
    private final DatosUsuarioFinder datosUsuarioFinder;
    private final SolicitudTieneRespuestasFinder solicitudTieneRespuestasFinder;
    private final ResponderSolicitudNovedadCoordinadorValidator validator;
    private final RespuestaOutputPort respuestaOutputPort;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(RespuestaNovedadCoordinadorDomain entrada) {
        logger.info(RespuestaKey.LOG_RESPONDIENDO,
                entrada.getSolicitud(), entrada.getCoordinadorUsuario());

        var resumen = datosSolicitudFinder.obtener(entrada.getSolicitud());
        var existe = !resumen.esVacio();
        var yaRespondida = solicitudTieneRespuestasFinder.obtener(entrada.getSolicitud());

        logger.debug(RespuestaKey.LOG_VERIFICACION_RESPUESTA, existe, yaRespondida);

        validator.validar(entrada.getSolicitud(), existe, resumen.tipoSolicitud(),
                resumen.destinatarioUsuario(), entrada.getCoordinadorUsuario(), yaRespondida);

        var respuesta = RespuestaDomain.crear(entrada.getSolicitud(), entrada.getContenido());
        respuestaOutputPort.registrar(RespuestaMapper.toEntity(respuesta));

        var remitente = datosUsuarioFinder.obtener(resumen.remitenteUsuario());
        var coordinador = datosUsuarioFinder.obtener(resumen.destinatarioUsuario());

        eventPublisher.publish(new SolicitudNovedadCoordinadorRespondidaEvent(
                entrada.getSolicitud(), respuesta.getId(), respuesta.getContenido(),
                respuesta.getEstadoRespuesta().getId(), remitente.getNombre(), remitente.getEmail(),
                coordinador.getNombre()));

        logger.info(RespuestaKey.LOG_RESPONDIDA, respuesta.getId());
        return respuesta.getId();
    }
}
