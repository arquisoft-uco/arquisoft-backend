package com.arquisoft.mapas_ruta.infrastructure.planestimado.command.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.application.planestimado.command.secondaryport.PlanEstimadoOutputPort;
import com.arquisoft.mapas_ruta.application.planestimado.command.secondaryport.entity.PlanEstimadoEntity;
import com.arquisoft.mapas_ruta.infrastructure.planestimado.command.secondaryadapter.mapper.PlanEstimadoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.PlanEstimadoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PlanEstimadoCommandOutputAdapter implements PlanEstimadoOutputPort {

    private final PlanEstimadoCommandRepository repository;
    private final AppLogger logger;

    @Override
    public void registrar(PlanEstimadoEntity entity) {
        repository.save(PlanEstimadoJpaMapper.toJpaEntity(entity));
        logger.debug(PlanEstimadoKey.LOG_GUARDADO, entity.id());
    }

    @Override
    public boolean existePorTarea(UUID tarea) {
        return repository.existsByTareaId(tarea);
    }

    @Override
    public boolean existePorId(UUID planEstimado) {
        return repository.existsById(planEstimado);
    }

    @Override
    public void actualizarDescripcion(UUID planEstimado, String descripcionEstimado) {
        repository.actualizarDescripcion(planEstimado, descripcionEstimado.isEmpty() ? null : descripcionEstimado);
        logger.debug(PlanEstimadoKey.LOG_GUARDADO, planEstimado);
    }

    @Override
    public void eliminar(UUID planEstimado) {
        repository.deleteById(planEstimado);
        logger.debug(PlanEstimadoKey.LOG_ELIMINADO, planEstimado);
    }

    @Override
    public long sumarDiasEstimadosPorMapaRutaDeTarea(UUID tarea) {
        return repository.sumarDiasEstimadosPorMapaRutaDeTarea(tarea);
    }
}
