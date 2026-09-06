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
import com.arquisoft.solicitudes.application.solicitud.command.finder.DatosUsuarioFinder;
import com.arquisoft.solicitudes.application.solicitud.command.finder.SolicitudDuplicadaFinder;
import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.SolicitudOutputPort;
import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.mapper.SolicitudMapper;
import com.arquisoft.solicitudes.application.solicitud.command.usecase.EnviarSolicitudNovedadAsesorUseCase;
import com.arquisoft.solicitudes.application.solicitud.command.validator.EnviarSolicitudNovedadAsesorValidator;
import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudNovedadAsesorDomain;
import com.arquisoft.solicitudes.domain.solicitud.SolicitudDomain;
import com.arquisoft.solicitudes.domain.solicitud.event.SolicitudNovedadAsesorEnviadaEvent;
import com.arquisoft.solicitudes.domain.solicitud.model.ClaveSolicitud;
import com.arquisoft.solicitudes.domain.solicitud.model.DisponibilidadSolicitud;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EnviarSolicitudNovedadAsesorUseCaseImpl
        implements EnviarSolicitudNovedadAsesorUseCase {

    private final SolicitudOutputPort solicitudOutputPort;
    private final RemitenteOutputPort remitenteOutputPort;
    private final DestinatarioOutputPort destinatarioOutputPort;
    private final RemitenteDeUsuarioFinder remitenteDeUsuarioFinder;
    private final DestinatarioDeUsuarioFinder destinatarioDeUsuarioFinder;
    private final DatosUsuarioFinder datosUsuarioFinder;
    private final SolicitudDuplicadaFinder solicitudDuplicadaFinder;
    private final EnviarSolicitudNovedadAsesorValidator validator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(EnvioSolicitudNovedadAsesorDomain envio) {
        logger.info(Mensajes.obtener(SolicitudKey.LOG_ENVIANDO_ASESOR),
                envio.getRemitenteUsuario(), envio.getDestinatarioUsuario());

        Optional<UsuarioDomain> remitenteUsuario = datosUsuarioFinder.obtener(envio.getRemitenteUsuario());
        Optional<UsuarioDomain> destinatarioUsuario =
                datosUsuarioFinder.obtener(envio.getDestinatarioUsuario());
        boolean remitenteUsuarioExiste = remitenteUsuario.isPresent();
        boolean destinatarioUsuarioExiste = destinatarioUsuario.isPresent();
        logger.debug(Mensajes.obtener(SolicitudKey.LOG_VERIFICACION_ENVIO_ASESOR),
                remitenteUsuarioExiste, destinatarioUsuarioExiste);
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
        logger.info(Mensajes.obtener(SolicitudKey.LOG_ENVIADA_ASESOR), solicitud.getId());

        eventPublisher.publish(new SolicitudNovedadAsesorEnviadaEvent(
                solicitud.getId(),
                envio.getRemitenteUsuario().toString(),
                envio.getDestinatarioUsuario().toString(),
                solicitud.getMensajeSolicitud(),
                solicitud.getFechaCreacion(),
                TipoSolicitud.NOVEDAD_PARA_EL_ASESOR.getId()));

        return solicitud.getId();
    }
}
