package com.arquisoft.solicitudes.application.respuesta.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.RespuestaKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.solicitudes.application.respuesta.command.secondaryport.RespuestaOutputPort;
import com.arquisoft.solicitudes.application.respuesta.command.secondaryport.mapper.RespuestaMapper;
import com.arquisoft.solicitudes.application.respuesta.command.usecase.ResponderSolicitudNovedadAsesorUseCase;
import com.arquisoft.solicitudes.application.respuesta.command.validator.ResponderSolicitudNovedadAsesorValidator;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DatosSolicitudFinder;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DatosUsuarioFinder;
import com.arquisoft.solicitudes.application.solicitud.command.finder.SolicitudTieneRespuestasFinder;
import com.arquisoft.solicitudes.domain.respuesta.RespuestaDomain;
import com.arquisoft.solicitudes.domain.respuesta.RespuestaNovedadAsesorDomain;
import com.arquisoft.solicitudes.domain.respuesta.event.SolicitudNovedadAsesorRespondidaEvent;
import com.arquisoft.solicitudes.domain.solicitud.model.ResumenSolicitud;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ResponderSolicitudNovedadAsesorUseCaseImpl
        implements ResponderSolicitudNovedadAsesorUseCase {

    private final DatosSolicitudFinder datosSolicitudFinder;
    private final DatosUsuarioFinder datosUsuarioFinder;
    private final SolicitudTieneRespuestasFinder solicitudTieneRespuestasFinder;
    private final ResponderSolicitudNovedadAsesorValidator validator;
    private final RespuestaOutputPort respuestaOutputPort;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(RespuestaNovedadAsesorDomain entrada) {
        logger.info(RespuestaKey.LOG_RESPONDIENDO,
                entrada.getSolicitud(), entrada.getAsesorUsuario());

        ResumenSolicitud resumen = datosSolicitudFinder.obtener(entrada.getSolicitud());
        boolean existe = !resumen.esVacio();
        boolean yaRespondida = solicitudTieneRespuestasFinder.obtener(entrada.getSolicitud());

        logger.debug(RespuestaKey.LOG_VERIFICACION_RESPUESTA, existe, yaRespondida);

        validator.validar(entrada.getSolicitud(), existe, resumen.tipoSolicitud(),
                resumen.destinatarioUsuario(), entrada.getAsesorUsuario(), yaRespondida);

        RespuestaDomain respuesta = RespuestaDomain.crear(
                entrada.getSolicitud(), entrada.getContenido());
        respuestaOutputPort.registrar(RespuestaMapper.toEntity(respuesta));

        UsuarioDomain remitente = datosUsuarioFinder.obtener(resumen.remitenteUsuario());
        UsuarioDomain asesor = datosUsuarioFinder.obtener(resumen.destinatarioUsuario());

        eventPublisher.publish(new SolicitudNovedadAsesorRespondidaEvent(
                entrada.getSolicitud(), respuesta.getId(), respuesta.getContenido(),
                respuesta.getEstadoRespuesta().getId(), remitente.getNombre(), remitente.getEmail(),
                asesor.getNombre()));

        logger.info(RespuestaKey.LOG_RESPONDIDA, respuesta.getId());
        return respuesta.getId();
    }
}
