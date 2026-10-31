package com.arquisoft.artefactos.infrastructure.observacion.command.secondaryadapter.repository;

import com.arquisoft.artefactos.application.observacion.command.secondaryport.ObservacionOutputPort;
import com.arquisoft.artefactos.application.observacion.command.secondaryport.entity.ObservacionEntity;
import com.arquisoft.artefactos.infrastructure.observacion.command.secondaryadapter.mapper.ObservacionJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ObservacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ObservacionCommandOutputAdapter implements ObservacionOutputPort {

    private final ObservacionCommandRepository observacionCommandRepository;
    private final AppLogger logger;

    @Override
    public void registrar(ObservacionEntity observacion) {
        observacionCommandRepository.save(ObservacionJpaMapper.toJpaEntity(observacion));
        logger.debug(ObservacionKey.LOG_GUARDADA, observacion.id());
    }

    @Override
    public boolean existePorRevisionAsesorYDescripcion(UUID revisionAsesor, String descripcion) {
        return observacionCommandRepository.existsByRevisionAsesorAndDescripcion(revisionAsesor, descripcion);
    }

    @Override
    public void eliminar(UUID observacion) {
        observacionCommandRepository.deleteById(observacion);
        logger.debug(ObservacionKey.LOG_ELIMINADA, observacion);
    }

    @Override
    public int avanzarEstadoPorRevisionAsesor(UUID revisionAsesor, String estadoOrigen, String estadoDestino) {
        var cantidad = observacionCommandRepository.avanzarEstadoPorRevisionAsesor(
                revisionAsesor, estadoOrigen, estadoDestino);
        logger.debug(ObservacionKey.LOG_ESTADOS_ACTUALIZADOS, revisionAsesor, cantidad);
        return cantidad;
    }

    @Override
    public Optional<ObservacionEntity> obtenerPorId(UUID id) {
        return observacionCommandRepository.findById(id).map(ObservacionJpaMapper::toEntity);
    }

    @Override
    public void actualizarEstado(UUID observacion, String estado) {
        observacionCommandRepository.actualizarEstado(observacion, estado);
        logger.debug(ObservacionKey.LOG_ESTADO_ACTUALIZADO, observacion);
    }
}
