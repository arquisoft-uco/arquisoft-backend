package com.arquisoft.artefactos.infrastructure.estudianteproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport.entity.EstudianteProyectoGradoEntity;
import com.arquisoft.artefactos.infrastructure.estudianteproyectogrado.command.secondaryadapter.mapper.EstudianteProyectoGradoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.EstudianteProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudianteProyectoGradoCommandOutputAdapter implements EstudianteProyectoGradoOutputPort {

    private final EstudianteProyectoGradoCommandRepository estudianteProyectoGradoCommandRepository;
    private final AppLogger logger;

    @Override
    public List<UUID> obtenerEstudiantesVigentesDelArtefacto(UUID artefacto, List<UUID> estudiantes) {
        return estudianteProyectoGradoCommandRepository.buscarEstudiantesVigentesDelArtefacto(artefacto, estudiantes);
    }

    @Override
    public List<EstudianteProyectoGradoEntity> obtenerPorProyectoGradoYEstudiantes(
            UUID proyectoGrado, List<UUID> estudiantes) {
        return estudianteProyectoGradoCommandRepository
                .findByProyectoGradoIdAndEstudianteIdIn(proyectoGrado, estudiantes)
                .stream()
                .map(EstudianteProyectoGradoJpaMapper::toEntity)
                .toList();
    }

    @Override
    public Optional<EstudianteProyectoGradoEntity> obtenerPorEstudianteYProyectoGrado(
            UUID estudiante, UUID proyectoGrado) {
        return estudianteProyectoGradoCommandRepository
                .findByEstudianteIdAndProyectoGradoId(estudiante, proyectoGrado)
                .map(EstudianteProyectoGradoJpaMapper::toEntity);
    }

    @Override
    public void guardar(EstudianteProyectoGradoEntity vinculo) {
        estudianteProyectoGradoCommandRepository.save(EstudianteProyectoGradoJpaMapper.toJpaEntity(vinculo));
        logger.debug(EstudianteProyectoGradoKey.LOG_GUARDADO, vinculo.id());
    }

    @Override
    public void guardarTodos(List<EstudianteProyectoGradoEntity> vinculos) {
        estudianteProyectoGradoCommandRepository.saveAll(
                vinculos.stream().map(EstudianteProyectoGradoJpaMapper::toJpaEntity).toList());
        logger.debug(EstudianteProyectoGradoKey.LOG_GUARDADOS, vinculos.size());
    }
}
