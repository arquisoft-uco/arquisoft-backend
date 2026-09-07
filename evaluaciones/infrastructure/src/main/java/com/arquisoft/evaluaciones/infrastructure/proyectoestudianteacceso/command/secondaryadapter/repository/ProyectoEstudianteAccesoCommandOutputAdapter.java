package com.arquisoft.evaluaciones.infrastructure.proyectoestudianteacceso.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.secondaryport.ProyectoEstudianteAccesoOutputPort;
import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.secondaryport.entity.ProyectoEstudianteAccesoEntity;
import com.arquisoft.evaluaciones.infrastructure.proyectoestudianteacceso.command.secondaryadapter.mapper.ProyectoEstudianteAccesoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ProyeccionAccesoEvaluacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProyectoEstudianteAccesoCommandOutputAdapter implements ProyectoEstudianteAccesoOutputPort {

    private final ProyectoEstudianteAccesoCommandRepository repository;
    private final AppLogger logger;

    @Override
    public Optional<ProyectoEstudianteAccesoEntity> buscarPorProyectoYEstudiante(UUID proyecto, UUID estudiante) {
        return repository.findByProyectoAndEstudiante(proyecto, estudiante)
                .map(ProyectoEstudianteAccesoJpaMapper::toEntity);
    }

    @Override
    public void guardar(ProyectoEstudianteAccesoEntity entity) {
        repository.save(ProyectoEstudianteAccesoJpaMapper.toJpaEntity(entity));
        logger.debug(ProyeccionAccesoEvaluacionKey.LOG_PROYECCION_ACTUALIZADA, entity.proyecto());
    }
}
