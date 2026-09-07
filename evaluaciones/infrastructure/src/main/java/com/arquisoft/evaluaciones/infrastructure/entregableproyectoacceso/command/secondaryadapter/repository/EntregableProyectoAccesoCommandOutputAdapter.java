package com.arquisoft.evaluaciones.infrastructure.entregableproyectoacceso.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.entregableproyectoacceso.command.secondaryport.EntregableProyectoAccesoOutputPort;
import com.arquisoft.evaluaciones.application.entregableproyectoacceso.command.secondaryport.entity.EntregableProyectoAccesoEntity;
import com.arquisoft.evaluaciones.infrastructure.entregableproyectoacceso.command.secondaryadapter.mapper.EntregableProyectoAccesoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ProyeccionAccesoEvaluacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EntregableProyectoAccesoCommandOutputAdapter implements EntregableProyectoAccesoOutputPort {

    private final EntregableProyectoAccesoCommandRepository repository;
    private final AppLogger logger;

    @Override
    public Optional<EntregableProyectoAccesoEntity> buscarPorEntregable(UUID entregable) {
        return repository.findById(entregable).map(EntregableProyectoAccesoJpaMapper::toEntity);
    }

    @Override
    public void guardar(EntregableProyectoAccesoEntity entity) {
        repository.save(EntregableProyectoAccesoJpaMapper.toJpaEntity(entity));
        logger.debug(ProyeccionAccesoEvaluacionKey.LOG_PROYECCION_ACTUALIZADA, entity.entregable());
    }
}
