package com.arquisoft.solicitudes.application.solicitud.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.SolicitudKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.solicitudes.application.destinatario.command.finder.DestinatarioDeUsuarioFinder;
import com.arquisoft.solicitudes.application.destinatario.command.secondaryport.DestinatarioOutputPort;
import com.arquisoft.solicitudes.application.destinatario.command.secondaryport.mapper.DestinatarioMapper;
import com.arquisoft.solicitudes.application.remitente.command.finder.RemitenteDeUsuarioFinder;
import com.arquisoft.solicitudes.application.remitente.command.secondaryport.RemitenteOutputPort;
import com.arquisoft.solicitudes.application.remitente.command.secondaryport.mapper.RemitenteMapper;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DatosUsuarioFinder;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DestinatarioAsignadoFinder;
import com.arquisoft.solicitudes.application.solicitud.command.finder.SolicitudDuplicadaFinder;
import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.SolicitudOutputPort;
import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.mapper.SolicitudMapper;
import com.arquisoft.solicitudes.application.solicitud.command.usecase.EnviarSolicitudAmpliacionPlazoUseCase;
import com.arquisoft.solicitudes.application.solicitud.command.validator.EnviarSolicitudAmpliacionPlazoValidator;
import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudAmpliacionPlazoDomain;
import com.arquisoft.solicitudes.domain.solicitud.SolicitudDomain;
import com.arquisoft.solicitudes.domain.solicitud.event.SolicitudAmpliacionPlazoEnviadaEvent;
import com.arquisoft.solicitudes.domain.solicitud.model.ClaveSolicitud;
import com.arquisoft.solicitudes.domain.solicitud.model.ConsultaAsignacionResponsable;
import com.arquisoft.solicitudes.domain.solicitud.model.DisponibilidadSolicitud;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EnviarSolicitudAmpliacionPlazoUseCaseImpl
        implements EnviarSolicitudAmpliacionPlazoUseCase {

    private final SolicitudOutputPort solicitudOutputPort;
    private final RemitenteOutputPort remitenteOutputPort;
    private final DestinatarioOutputPort destinatarioOutputPort;
    private final RemitenteDeUsuarioFinder remitenteDeUsuarioFinder;
    private final DestinatarioDeUsuarioFinder destinatarioDeUsuarioFinder;
    private final DatosUsuarioFinder datosUsuarioFinder;
    private final DestinatarioAsignadoFinder destinatarioAsignadoFinder;
    private final SolicitudDuplicadaFinder solicitudDuplicadaFinder;
    private final EnviarSolicitudAmpliacionPlazoValidator validator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(EnvioSolicitudAmpliacionPlazoDomain envio) {
        logger.info(SolicitudKey.LOG_ENVIANDO_AMPLIACION_PLAZO,
                envio.getRemitenteUsuario(), envio.getDestinatarioUsuario());

        Optional<UsuarioDomain> remitenteUsuario = datosUsuarioFinder.obtener(envio.getRemitenteUsuario());
        Optional<UsuarioDomain> destinatarioUsuario =
                datosUsuarioFinder.obtener(envio.getDestinatarioUsuario());
        boolean remitenteUsuarioExiste = remitenteUsuario.isPresent();
        boolean destinatarioUsuarioExiste = destinatarioUsuario.isPresent();
        logger.debug(SolicitudKey.LOG_VERIFICACION_ENVIO_AMPLIACION_PLAZO,
                remitenteUsuarioExiste, destinatarioUsuarioExiste);
        validator.validarExistenciaUsuarios(envio, remitenteUsuarioExiste, destinatarioUsuarioExiste);

        boolean destinatarioAsignado = destinatarioAsignadoFinder.obtener(
                new ConsultaAsignacionResponsable(envio.getRemitenteUsuario(), envio.getDestinatarioUsuario()));
        validator.validarAsignacionDestinatario(envio, destinatarioAsignado);

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

        UsuarioDomain remitente = remitenteUsuario.orElseThrow();
        UsuarioDomain destinatario = destinatarioUsuario.orElseThrow();
        eventPublisher.publish(new SolicitudAmpliacionPlazoEnviadaEvent(
                solicitud.getId(), remitente.getNombre(), destinatario.getNombre(),
                destinatario.getEmail(), solicitud.getMensajeSolicitud()));

        logger.info(SolicitudKey.LOG_ENVIADA_AMPLIACION_PLAZO, solicitud.getId());
        return solicitud.getId();
    }
}
