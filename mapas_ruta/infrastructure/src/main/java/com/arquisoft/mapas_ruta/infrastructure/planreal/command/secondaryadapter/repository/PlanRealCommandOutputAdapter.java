package com.arquisoft.mapas_ruta.infrastructure.planreal.command.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.application.planreal.command.secondaryport.PlanRealOutputPort;
import com.arquisoft.mapas_ruta.application.planreal.command.secondaryport.entity.PlanRealEntity;
import com.arquisoft.mapas_ruta.infrastructure.planreal.command.secondaryadapter.mapper.PlanRealJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.PlanRealKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PlanRealCommandOutputAdapter implements PlanRealOutputPort {

    private final PlanRealCommandRepository repository;
    private final AppLogger logger;

    @Override
    public void registrar(PlanRealEntity entity) {
        repository.save(PlanRealJpaMapper.toJpaEntity(entity));
        logger.debug(PlanRealKey.LOG_GUARDADO, entity.tarea());
    }

    @Override
    public boolean existePorTarea(UUID tarea) {
        return repository.existsByTareaId(tarea);
    }

    @Override
    public void actualizarPorTarea(UUID tarea, int tiempoRealDias, String descripcionReal) {
        repository.actualizarPorTarea(tarea, tiempoRealDias, descripcionReal.isEmpty() ? null : descripcionReal);
        logger.debug(PlanRealKey.LOG_GUARDADO, tarea);
    }

    @Override
    public boolean existePorId(UUID planReal) {
        return repository.existsById(planReal);
    }

    @Override
    public void actualizarPorId(UUID planReal, int tiempoRealDias, String descripcionReal) {
        repository.actualizarPorId(planReal, tiempoRealDias, descripcionReal.isEmpty() ? null : descripcionReal);
        logger.debug(PlanRealKey.LOG_ACTUALIZADO, planReal);
    }

    @Override
    public void eliminarPorId(UUID planReal) {
        repository.deleteById(planReal);
        logger.debug(PlanRealKey.LOG_ELIMINADO, planReal);
    }
}
