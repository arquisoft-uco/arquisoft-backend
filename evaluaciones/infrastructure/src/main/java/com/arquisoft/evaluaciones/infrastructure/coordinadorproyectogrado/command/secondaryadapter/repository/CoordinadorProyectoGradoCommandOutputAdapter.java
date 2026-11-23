package com.arquisoft.evaluaciones.infrastructure.coordinadorproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.secondaryport.CoordinadorProyectoGradoOutputPort;
import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.secondaryport.entity.CoordinadorProyectoGradoEntity;
import com.arquisoft.evaluaciones.infrastructure.coordinadorproyectogrado.command.secondaryadapter.mapper.CoordinadorProyectoGradoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.CoordinadorProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CoordinadorProyectoGradoCommandOutputAdapter implements CoordinadorProyectoGradoOutputPort {

    private final CoordinadorProyectoGradoCommandRepository repository;
    private final AppLogger logger;

    @Override
    public void guardar(CoordinadorProyectoGradoEntity coordinador) {
        repository.save(CoordinadorProyectoGradoJpaMapper.toJpaEntity(coordinador));
        logger.debug(CoordinadorProyectoGradoKey.LOG_GUARDADO, coordinador.proyectoGrado());
    }

    @Override
    public boolean existePorProyectoGrado(UUID proyectoGrado) {
        return repository.existsById(proyectoGrado);
    }

    @Override
    public Optional<CoordinadorProyectoGradoEntity> obtenerPorEvaluacion(UUID evaluacion) {
        return repository.findPorEvaluacion(evaluacion).map(CoordinadorProyectoGradoJpaMapper::toEntity);
    }

    @Override
    public Optional<CoordinadorProyectoGradoEntity> obtenerPorProyectoGrado(UUID proyectoGrado) {
        return repository.findById(proyectoGrado).map(CoordinadorProyectoGradoJpaMapper::toEntity);
    }
}
