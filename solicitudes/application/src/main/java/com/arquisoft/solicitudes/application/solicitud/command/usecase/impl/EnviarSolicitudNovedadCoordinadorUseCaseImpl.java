package com.arquisoft.solicitudes.application.solicitud.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.SolicitudKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilUUID;
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
import com.arquisoft.solicitudes.application.solicitud.command.usecase.EnviarSolicitudNovedadCoordinadorUseCase;
import com.arquisoft.solicitudes.application.solicitud.command.validator.EnviarSolicitudNovedadCoordinadorValidator;
import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudNovedadCoordinadorDomain;
import com.arquisoft.solicitudes.domain.solicitud.SolicitudDomain;
import com.arquisoft.solicitudes.domain.solicitud.event.SolicitudNovedadCoordinadorEnviadaEvent;
import com.arquisoft.solicitudes.domain.solicitud.model.ClaveSolicitud;
import com.arquisoft.solicitudes.domain.solicitud.model.ConsultaAsignacionResponsable;
import com.arquisoft.solicitudes.domain.solicitud.model.DisponibilidadSolicitud;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;
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
    private final DatosUsuarioFinder datosUsuarioFinder;
    private final DestinatarioAsignadoFinder destinatarioAsignadoFinder;
    private final SolicitudDuplicadaFinder solicitudDuplicadaFinder;
    private final EnviarSolicitudNovedadCoordinadorValidator validator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(EnvioSolicitudNovedadCoordinadorDomain envio) {
        UsuarioDomain remitenteUsuario =
                datosUsuarioFinder.obtener(envio.getRemitenteUsuario());
        UsuarioDomain destinatarioUsuario =
                datosUsuarioFinder.obtener(envio.getDestinatarioUsuario());
        validator.validarExistenciaUsuarios(envio, remitenteUsuario, destinatarioUsuario);

        boolean destinatarioAsignado = destinatarioAsignadoFinder.obtener(new ConsultaAsignacionResponsable(
                envio.getRemitenteUsuario(), envio.getDestinatarioUsuario()));
        validator.validarAsignacionDestinatario(envio, destinatarioAsignado);

        var remitenteId = resolverRemitente(envio);
        var destinatarioId = resolverDestinatario(envio);

        SolicitudDomain solicitud = SolicitudDomain.crear(
                destinatarioId, remitenteId,
                envio.getSolicitud().getMensajeSolicitud(),
                envio.getSolicitud().getTipoSolicitud());

        ClaveSolicitud clave = new ClaveSolicitud(destinatarioId, remitenteId,
                solicitud.getFechaCreacion(), solicitud.getMensajeSolicitud());
        boolean yaExiste = solicitudDuplicadaFinder.obtener(clave);
        validator.validarUnicidad(new DisponibilidadSolicitud(clave, yaExiste));

        solicitudOutputPort.registrar(SolicitudMapper.toEntity(solicitud));

        eventPublisher.publish(new SolicitudNovedadCoordinadorEnviadaEvent(
                solicitud.getId(), remitenteUsuario.getNombre(),
                destinatarioUsuario.getNombre(), destinatarioUsuario.getEmail(),
                solicitud.getMensajeSolicitud()));

        logger.info(SolicitudKey.LOG_ENVIADA, solicitud.getId());
        return solicitud.getId();
    }

    private UUID resolverRemitente(EnvioSolicitudNovedadCoordinadorDomain envio) {
        var remitenteId = remitenteDeUsuarioFinder.obtener(envio.getRemitenteUsuario());
        if (!UtilUUID.esPorDefecto(remitenteId)) {
            return remitenteId;
        }
        remitenteOutputPort.registrar(RemitenteMapper.toEntity(envio.getRemitente()));
        return envio.getRemitente().getId();
    }

    private UUID resolverDestinatario(EnvioSolicitudNovedadCoordinadorDomain envio) {
        var destinatarioId = destinatarioDeUsuarioFinder.obtener(envio.getDestinatarioUsuario());
        if (!UtilUUID.esPorDefecto(destinatarioId)) {
            return destinatarioId;
        }
        destinatarioOutputPort.registrar(DestinatarioMapper.toEntity(envio.getDestinatario()));
        return envio.getDestinatario().getId();
    }
}
