package com.arquisoft.solicitudes.infrastructure.solicitud.command.secondaryadapter.repository;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.SolicitudKey;
import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.SolicitudOutputPort;
import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.entity.DatosSolicitudEntity;
import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.entity.SolicitudEntity;
import com.arquisoft.solicitudes.infrastructure.solicitud.command.secondaryadapter.mapper.SolicitudJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SolicitudCommandOutputAdapter implements SolicitudOutputPort {

    private final SolicitudCommandRepository solicitudCommandRepository;
    private final AppLogger logger;

    @Override
    public void registrar(SolicitudEntity solicitud) {
        solicitudCommandRepository.save(SolicitudJpaMapper.toJpaEntity(solicitud));
        logger.debug(SolicitudKey.LOG_GUARDADA, solicitud.id());
    }

    @Override
    public boolean existePorCombinacionUnica(
            UUID destinatario, UUID remitente, LocalDateTime fechaCreacion, String mensajeSolicitud) {
        return solicitudCommandRepository.existePorCombinacionUnica(
                destinatario, remitente, fechaCreacion, mensajeSolicitud);
    }

    @Override
    public Optional<DatosSolicitudEntity> buscarDatos(UUID solicitudId) {
        return solicitudCommandRepository.buscarDatos(solicitudId);
    }

    @Override
    public boolean tieneRespuestas(UUID solicitudId) {
        return solicitudCommandRepository.tieneRespuestas(solicitudId);
    }

    @Override
    public void eliminar(UUID solicitudId) {
        solicitudCommandRepository.deleteById(solicitudId);
        logger.debug(SolicitudKey.LOG_ELIMINADA_REGISTRO, solicitudId);
    }
}
