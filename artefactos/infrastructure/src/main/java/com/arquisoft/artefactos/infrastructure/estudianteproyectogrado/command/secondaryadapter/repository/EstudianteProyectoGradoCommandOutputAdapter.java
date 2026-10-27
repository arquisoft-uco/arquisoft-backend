package com.arquisoft.artefactos.infrastructure.estudianteproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport.entity.PertenenciaProyectoGradoEntity;
import com.arquisoft.artefactos.infrastructure.estudianteproyectogrado.command.secondaryadapter.mapper.EstudianteProyectoGradoJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudianteProyectoGradoCommandOutputAdapter implements EstudianteProyectoGradoOutputPort {

    private final EstudianteProyectoGradoCommandRepository estudianteProyectoGradoCommandRepository;

    @Override
    public List<PertenenciaProyectoGradoEntity> obtenerPertenenciasVigentes(
            List<UUID> artefactos, List<UUID> estudiantes) {
        return estudianteProyectoGradoCommandRepository.buscarPertenenciasVigentes(artefactos, estudiantes)
                .stream()
                .map(EstudianteProyectoGradoJpaMapper::toEntity)
                .toList();
    }
}
