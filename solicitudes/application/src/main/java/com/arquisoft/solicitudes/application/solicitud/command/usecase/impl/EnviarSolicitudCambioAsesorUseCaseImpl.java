package com.arquisoft.solicitudes.application.solicitud.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.SolicitudKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.solicitudes.application.destinatario.command.usecase.RegistrarDestinatarioUseCase;
import com.arquisoft.solicitudes.application.remitente.command.usecase.RegistrarRemitenteUseCase;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DatosUsuarioFinder;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DestinatarioAsignadoFinder;
import com.arquisoft.solicitudes.application.solicitud.command.finder.SolicitudDuplicadaFinder;
import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.SolicitudOutputPort;
import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.mapper.SolicitudMapper;
import com.arquisoft.solicitudes.application.solicitud.command.usecase.EnviarSolicitudCambioAsesorUseCase;
import com.arquisoft.solicitudes.application.solicitud.command.validator.EnviarSolicitudCambioAsesorValidator;
import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudCambioAsesorDomain;
import com.arquisoft.solicitudes.domain.solicitud.SolicitudDomain;
import com.arquisoft.solicitudes.domain.solicitud.event.SolicitudCambioAsesorEnviadaEvent;
import com.arquisoft.solicitudes.domain.solicitud.model.ClaveSolicitud;
import com.arquisoft.solicitudes.domain.solicitud.model.ConsultaAsignacionResponsable;
import com.arquisoft.solicitudes.domain.solicitud.model.DisponibilidadSolicitud;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EnviarSolicitudCambioAsesorUseCaseImpl
        implements EnviarSolicitudCambioAsesorUseCase {

    private final SolicitudOutputPort solicitudOutputPort;
    private final RegistrarRemitenteUseCase registrarRemitenteUseCase;
    private final RegistrarDestinatarioUseCase registrarDestinatarioUseCase;
    private final DatosUsuarioFinder datosUsuarioFinder;
    private final DestinatarioAsignadoFinder destinatarioAsignadoFinder;
    private final SolicitudDuplicadaFinder solicitudDuplicadaFinder;
    private final EnviarSolicitudCambioAsesorValidator validator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(EnvioSolicitudCambioAsesorDomain envio) {
        logger.info(SolicitudKey.LOG_ENVIANDO_CAMBIO_ASESOR,
                envio.getRemitenteUsuario(), envio.getDestinatarioUsuario());

        UsuarioDomain remitenteUsuario =
                datosUsuarioFinder.obtener(envio.getRemitenteUsuario());
        UsuarioDomain destinatarioUsuario =
                datosUsuarioFinder.obtener(envio.getDestinatarioUsuario());
        logger.debug(SolicitudKey.LOG_VERIFICACION_ENVIO_CAMBIO_ASESOR,
                !remitenteUsuario.esVacio(), !destinatarioUsuario.esVacio());
        validator.validarExistenciaUsuarios(envio, remitenteUsuario, destinatarioUsuario);

        boolean destinatarioAsignado = destinatarioAsignadoFinder.obtener(
                new ConsultaAsignacionResponsable(envio.getRemitenteUsuario(), envio.getDestinatarioUsuario()));
        validator.validarAsignacionDestinatario(envio, destinatarioAsignado);

        var remitenteId = registrarRemitenteUseCase.ejecutar(envio.getRemitente());
        var destinatarioId = registrarDestinatarioUseCase.ejecutar(envio.getDestinatario());

        SolicitudDomain solicitud = SolicitudDomain.crear(
                destinatarioId, remitenteId,
                envio.getSolicitud().getMensajeSolicitud(),
                envio.getSolicitud().getTipoSolicitud());

        ClaveSolicitud clave = new ClaveSolicitud(destinatarioId, remitenteId,
                solicitud.getFechaCreacion(), solicitud.getMensajeSolicitud());
        boolean yaExiste = solicitudDuplicadaFinder.obtener(clave);
        validator.validarUnicidad(new DisponibilidadSolicitud(clave, yaExiste));

        solicitudOutputPort.registrar(SolicitudMapper.toEntity(solicitud));

        eventPublisher.publish(new SolicitudCambioAsesorEnviadaEvent(
                solicitud.getId(), remitenteUsuario.getNombre(), destinatarioUsuario.getNombre(),
                destinatarioUsuario.getEmail(), solicitud.getMensajeSolicitud()));

        logger.info(SolicitudKey.LOG_ENVIADA_CAMBIO_ASESOR, solicitud.getId());
        return solicitud.getId();
    }
}
