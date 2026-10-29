package com.arquisoft.entregables.infrastructure.estudianteproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.entregables.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.entregables.application.estudianteproyectogrado.command.secondaryport.entity.EstudianteProyectoGradoEntity;
import com.arquisoft.entregables.infrastructure.estudianteproyectogrado.command.secondaryadapter.mapper.EstudianteProyectoGradoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.EstudianteProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudianteProyectoGradoCommandOutputAdapter implements EstudianteProyectoGradoOutputPort {

    private final EstudianteProyectoGradoCommandRepository repository;
    private final AppLogger logger;

    @Override
    public List<EstudianteProyectoGradoEntity> consultarPorProyectoGrado(UUID proyectoGrado) {
        return repository.findByProyectoGradoId(proyectoGrado).stream()
                .map(EstudianteProyectoGradoJpaMapper::toEntity)
                .toList();
    }

    @Override
    public Optional<EstudianteProyectoGradoEntity> obtenerPorEstudianteYProyectoGrado(
            UUID estudiante, UUID proyectoGrado) {
        return repository.findByEstudianteIdAndProyectoGradoId(estudiante, proyectoGrado)
                .map(EstudianteProyectoGradoJpaMapper::toEntity);
    }

    @Override
    public void guardarTodos(List<EstudianteProyectoGradoEntity> vinculos) {
        repository.saveAll(vinculos.stream().map(EstudianteProyectoGradoJpaMapper::toJpaEntity).toList());
        logger.debug(EstudianteProyectoGradoKey.LOG_GUARDADOS, vinculos.size());
    }

    @Override
    public void guardar(EstudianteProyectoGradoEntity vinculo) {
        repository.save(EstudianteProyectoGradoJpaMapper.toJpaEntity(vinculo));
        logger.debug(EstudianteProyectoGradoKey.LOG_GUARDADOS, 1);
    }
}
