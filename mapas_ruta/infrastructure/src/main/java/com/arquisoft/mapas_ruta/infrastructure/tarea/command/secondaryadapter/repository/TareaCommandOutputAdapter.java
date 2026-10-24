package com.arquisoft.mapas_ruta.infrastructure.tarea.command.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.application.tarea.command.secondaryport.TareaOutputPort;
import com.arquisoft.mapas_ruta.application.tarea.command.secondaryport.entity.TareaEntity;
import com.arquisoft.mapas_ruta.infrastructure.tarea.command.secondaryadapter.mapper.TareaJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.TareaKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TareaCommandOutputAdapter implements TareaOutputPort {

    private final TareaCommandRepository repository;
    private final AppLogger logger;

    @Override
    public void registrar(TareaEntity entity) {
        repository.save(TareaJpaMapper.toJpaEntity(entity));
        logger.debug(TareaKey.LOG_GUARDADA, entity.id());
    }

    @Override
    public Optional<TareaEntity> obtenerPorId(UUID tarea) {
        return repository.findById(tarea).map(TareaJpaMapper::toEntity);
    }

    @Override
    public void actualizarEstadoItem(UUID tarea, String estadoItem) {
        repository.actualizarEstadoItem(tarea, estadoItem);
        logger.debug(TareaKey.LOG_GUARDADA, tarea);
    }

    @Override
    public boolean existePorMapaRutaYDescripcion(UUID mapaRuta, String descripcion) {
        return repository.existsByMapaRutaIdAndDescripcion(mapaRuta, descripcion);
    }
}
