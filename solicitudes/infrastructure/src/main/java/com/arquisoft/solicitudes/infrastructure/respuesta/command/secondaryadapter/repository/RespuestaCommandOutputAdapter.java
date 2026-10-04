package com.arquisoft.solicitudes.infrastructure.respuesta.command.secondaryadapter.repository;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.RespuestaKey;
import com.arquisoft.solicitudes.application.respuesta.command.secondaryport.RespuestaOutputPort;
import com.arquisoft.solicitudes.application.respuesta.command.secondaryport.entity.RespuestaEntity;
import com.arquisoft.solicitudes.infrastructure.respuesta.command.secondaryadapter.mapper.EstadoRespuestaJpaMapper;
import com.arquisoft.solicitudes.infrastructure.respuesta.command.secondaryadapter.mapper.RespuestaJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RespuestaCommandOutputAdapter implements RespuestaOutputPort {

    private final RespuestaCommandRepository respuestaCommandRepository;
    private final AppLogger logger;

    @Override
    public void registrar(RespuestaEntity respuesta) {
        respuestaCommandRepository.save(RespuestaJpaMapper.toJpaEntity(respuesta));
        logger.debug(RespuestaKey.LOG_GUARDADA, respuesta.id());
    }

    @Override
    public boolean existePorSolicitud(UUID solicitudId) {
        return respuestaCommandRepository.existsBySolicitudId(solicitudId);
    }

    @Override
    public Optional<String> buscarEstadoPorSolicitud(UUID solicitudId) {
        return respuestaCommandRepository.buscarEstadoPorSolicitud(solicitudId);
    }

    @Override
    public void eliminarPorSolicitud(UUID solicitudId) {
        respuestaCommandRepository.deleteBySolicitudId(solicitudId);
        logger.debug(RespuestaKey.LOG_ELIMINADA_REGISTRO, solicitudId);
    }

    @Override
    public void actualizarEstadoPorSolicitud(UUID solicitudId, String nuevoEstado) {
        respuestaCommandRepository.actualizarEstadoPorSolicitud(
                solicitudId, EstadoRespuestaJpaMapper.toReferencia(nuevoEstado));
        logger.debug(RespuestaKey.LOG_GUARDADA, solicitudId);
    }
}
