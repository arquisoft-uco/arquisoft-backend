package com.arquisoft.artefactos.infrastructure.estudianteartefacto.command.secondaryadapter.repository;

import com.arquisoft.artefactos.application.estudianteartefacto.command.secondaryport.EstudianteArtefactoOutputPort;
import com.arquisoft.artefactos.application.estudianteartefacto.command.secondaryport.entity.EstudianteArtefactoEntity;
import com.arquisoft.artefactos.infrastructure.estudianteartefacto.command.secondaryadapter.mapper.EstudianteArtefactoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.EstudianteArtefactoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudianteArtefactoCommandOutputAdapter implements EstudianteArtefactoOutputPort {

    private final EstudianteArtefactoCommandRepository estudianteArtefactoCommandRepository;
    private final AppLogger logger;

    @Override
    public boolean existePorArtefactoYEstudiante(UUID artefacto, UUID estudiante) {
        return estudianteArtefactoCommandRepository.existsByArtefactoAndEstudiante(artefacto, estudiante);
    }

    @Override
    public void vincularTodos(List<EstudianteArtefactoEntity> vinculos) {
        estudianteArtefactoCommandRepository.saveAll(
                vinculos.stream().map(EstudianteArtefactoJpaMapper::toJpaEntity).toList());
        logger.debug(EstudianteArtefactoKey.LOG_GUARDADOS, vinculos.size());
    }

    @Override
    public List<EstudianteArtefactoEntity> obtenerVinculados(UUID artefacto, List<UUID> estudiantes) {
        return estudianteArtefactoCommandRepository.findByArtefactoAndEstudianteIn(artefacto, estudiantes)
                .stream()
                .map(EstudianteArtefactoJpaMapper::toEntity)
                .toList();
    }

    @Override
    public void eliminarPorProyectoGradoYEstudiante(UUID proyectoGrado, UUID estudiante) {
        estudianteArtefactoCommandRepository.eliminarPorProyectoGradoYEstudiante(proyectoGrado, estudiante);
        logger.debug(EstudianteArtefactoKey.LOG_DESVINCULADOS, proyectoGrado, estudiante);
    }
}
