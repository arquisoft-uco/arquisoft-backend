package com.arquisoft.solicitudes.application.solicitud.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.solicitudes.SolicitudKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.solicitudes.application.destinatario.command.finder.DestinatarioDeUsuarioFinder;
import com.arquisoft.solicitudes.application.destinatario.command.secondaryport.DestinatarioOutputPort;
import com.arquisoft.solicitudes.application.destinatario.command.secondaryport.mapper.DestinatarioMapper;
import com.arquisoft.solicitudes.application.remitente.command.finder.RemitenteDeUsuarioFinder;
import com.arquisoft.solicitudes.application.remitente.command.secondaryport.RemitenteOutputPort;
import com.arquisoft.solicitudes.application.remitente.command.secondaryport.mapper.RemitenteMapper;
import com.arquisoft.solicitudes.application.solicitud.command.finder.SolicitudDuplicadaFinder;
import com.arquisoft.solicitudes.application.solicitud.command.finder.UsuarioExisteFinder;
import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.SolicitudOutputPort;
import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.mapper.SolicitudMapper;
import com.arquisoft.solicitudes.application.solicitud.command.usecase.EnviarSolicitudNovedadCoordinadorUseCase;
import com.arquisoft.solicitudes.application.solicitud.command.validator.EnviarSolicitudNovedadCoordinadorValidator;
import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudNovedadCoordinadorDomain;
import com.arquisoft.solicitudes.domain.solicitud.SolicitudDomain;
import com.arquisoft.solicitudes.domain.solicitud.event.SolicitudNovedadCoordinadorEnviadaEvent;
import com.arquisoft.solicitudes.domain.solicitud.model.ClaveSolicitud;
import com.arquisoft.solicitudes.domain.solicitud.model.DisponibilidadSolicitud;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EnviarSolicitudNovedadCoordinadorUseCaseImpl
        implements EnviarSolicitudNovedadCoordinadorUseCase {

    private final SolicitudOutputPort solicitudOutputPort;
    private final RemitenteOutputPort remitenteOutputPort;
    private final DestinatarioOutputPort destinatarioOutputPort;
    private final RemitenteDeUsuarioFinder remitenteDeUsuarioFinder;
    private final DestinatarioDeUsuarioFinder destinatarioDeUsuarioFinder;
    private final UsuarioExisteFinder usuarioExisteFinder;
    private final SolicitudDuplicadaFinder solicitudDuplicadaFinder;
    private final EnviarSolicitudNovedadCoordinadorValidator validator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(EnvioSolicitudNovedadCoordinadorDomain envio) {
        boolean remitenteUsuarioExiste = usuarioExisteFinder.obtener(envio.getRemitenteUsuario());
        boolean destinatarioUsuarioExiste = usuarioExisteFinder.obtener(envio.getDestinatarioUsuario());
        validator.validarExistenciaUsuarios(envio, remitenteUsuarioExiste, destinatarioUsuarioExiste);

        UUID remitenteId = remitenteDeUsuarioFinder.obtener(envio.getRemitenteUsuario())
                .orElseGet(() -> {
                    remitenteOutputPort.registrar(RemitenteMapper.toEntity(envio.getRemitente()));
                    return envio.getRemitente().getId();
                });

        UUID destinatarioId = destinatarioDeUsuarioFinder.obtener(envio.getDestinatarioUsuario())
                .orElseGet(() -> {
                    destinatarioOutputPort.registrar(DestinatarioMapper.toEntity(envio.getDestinatario()));
                    return envio.getDestinatario().getId();
                });

        SolicitudDomain solicitud = SolicitudDomain.crear(
                destinatarioId, remitenteId,
                envio.getSolicitud().getMensajeSolicitud(),
                envio.getSolicitud().getTipoSolicitud());

        ClaveSolicitud clave = new ClaveSolicitud(destinatarioId, remitenteId,
                solicitud.getFechaCreacion(), solicitud.getMensajeSolicitud());
        boolean yaExiste = solicitudDuplicadaFinder.obtener(clave);
        validator.validarUnicidad(new DisponibilidadSolicitud(clave, yaExiste));

        solicitudOutputPort.registrar(SolicitudMapper.toEntity(solicitud));
        logger.info(Mensajes.obtener(SolicitudKey.LOG_ENVIADA), solicitud.getId());

        eventPublisher.publish(new SolicitudNovedadCoordinadorEnviadaEvent(
                solicitud.getId(),
                envio.getRemitenteUsuario().toString(),
                envio.getDestinatarioUsuario().toString(),
                solicitud.getMensajeSolicitud(),
                solicitud.getFechaCreacion(),
                TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId()));

        return solicitud.getId();
    }
}
