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
    public void actualizarInformacion(UUID tarea, String descripcion, UUID asesor) {
        repository.actualizarInformacion(tarea, descripcion, asesor);
        logger.debug(TareaKey.LOG_GUARDADA, tarea);
    }

    @Override
    public void eliminar(UUID tarea) {
        repository.deleteById(tarea);
        logger.debug(TareaKey.LOG_ELIMINADA, tarea);
    }

    @Override
    public boolean existePorMapaRutaYDescripcion(UUID mapaRuta, String descripcion) {
        return repository.existsByMapaRutaIdAndDescripcion(mapaRuta, descripcion);
    }

    @Override
    public boolean existeOtraPorMapaRutaYDescripcion(UUID mapaRuta, String descripcion, UUID tarea) {
        return repository.existsByMapaRutaIdAndDescripcionAndIdNot(mapaRuta, descripcion, tarea);
    }
}
