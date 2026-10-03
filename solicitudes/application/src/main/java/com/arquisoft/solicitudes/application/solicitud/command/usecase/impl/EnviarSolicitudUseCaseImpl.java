package com.arquisoft.solicitudes.application.solicitud.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.SolicitudKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.solicitudes.application.destinatario.command.finder.DestinatarioDeUsuarioFinder;
import com.arquisoft.solicitudes.application.destinatario.command.usecase.RegistrarDestinatarioUseCase;
import com.arquisoft.solicitudes.application.remitente.command.finder.RemitenteDeUsuarioFinder;
import com.arquisoft.solicitudes.application.remitente.command.usecase.RegistrarRemitenteUseCase;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DestinatarioAsignadoFinder;
import com.arquisoft.solicitudes.application.solicitud.command.finder.SolicitudDuplicadaFinder;
import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.SolicitudOutputPort;
import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.mapper.SolicitudMapper;
import com.arquisoft.solicitudes.application.solicitud.command.usecase.EnviarSolicitudUseCase;
import com.arquisoft.solicitudes.application.solicitud.command.validator.EnviarSolicitudValidator;
import com.arquisoft.solicitudes.application.usuario.command.finder.UsuarioPorIdFinder;
import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudDomain;
import com.arquisoft.solicitudes.domain.solicitud.event.SolicitudEnviadaEvent;
import com.arquisoft.solicitudes.domain.solicitud.model.ClaveSolicitud;
import com.arquisoft.solicitudes.domain.solicitud.model.ConsultaAsignacionResponsable;
import com.arquisoft.solicitudes.domain.solicitud.model.DisponibilidadSolicitud;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EnviarSolicitudUseCaseImpl implements EnviarSolicitudUseCase {

    private final SolicitudOutputPort solicitudOutputPort;
    private final RegistrarRemitenteUseCase registrarRemitenteUseCase;
    private final RegistrarDestinatarioUseCase registrarDestinatarioUseCase;
    private final RemitenteDeUsuarioFinder remitenteDeUsuarioFinder;
    private final DestinatarioDeUsuarioFinder destinatarioDeUsuarioFinder;
    private final UsuarioPorIdFinder usuarioPorIdFinder;
    private final DestinatarioAsignadoFinder destinatarioAsignadoFinder;
    private final SolicitudDuplicadaFinder solicitudDuplicadaFinder;
    private final EnviarSolicitudValidator validator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(EnvioSolicitudDomain envio) {
        var solicitud = envio.getSolicitud();
        var tipo = solicitud.getTipoSolicitud().getId();

        logger.info(SolicitudKey.LOG_ENVIANDO,
                tipo, envio.getRemitenteUsuario(), envio.getDestinatarioUsuario());

        registrarRemitenteUseCase.ejecutar(envio.getRemitente());
        registrarDestinatarioUseCase.ejecutar(envio.getDestinatario());

        var remitenteId = remitenteDeUsuarioFinder.obtener(envio.getRemitenteUsuario());
        var destinatarioId = destinatarioDeUsuarioFinder.obtener(envio.getDestinatarioUsuario());
        var remitente = usuarioPorIdFinder.obtener(envio.getRemitenteUsuario());
        var destinatario = usuarioPorIdFinder.obtener(envio.getDestinatarioUsuario());
        var clave = new ClaveSolicitud(destinatarioId, remitenteId,
                solicitud.getFechaCreacion(), solicitud.getMensajeSolicitud());
        var destinatarioAsignado = destinatarioAsignadoFinder.obtener(
                new ConsultaAsignacionResponsable(envio.getRemitenteUsuario(), envio.getDestinatarioUsuario()));
        var yaExiste = solicitudDuplicadaFinder.obtener(clave);

        logger.debug(SolicitudKey.LOG_VERIFICACION_ENVIO, destinatarioAsignado, yaExiste);

        validator.validar(envio, destinatarioAsignado, new DisponibilidadSolicitud(clave, yaExiste));

        solicitudOutputPort.registrar(SolicitudMapper.toEntity(solicitud, remitenteId, destinatarioId));
        eventPublisher.publish(new SolicitudEnviadaEvent(solicitud.getTipoSolicitud(), solicitud.getId(),
                remitente.getNombre(), destinatario.getNombre(), destinatario.getEmail(),
                solicitud.getMensajeSolicitud()));

        logger.info(SolicitudKey.LOG_ENVIADA, tipo, solicitud.getId());
        return solicitud.getId();
    }
}
