package com.arquisoft.artefactos.application.estudianteartefacto.command.finder.impl;

import com.arquisoft.artefactos.application.estudianteartefacto.command.finder.EstudiantesYaVinculadosArtefactoFinder;
import com.arquisoft.artefactos.application.estudianteartefacto.command.secondaryport.EstudianteArtefactoOutputPort;
import com.arquisoft.artefactos.application.estudianteartefacto.command.secondaryport.entity.EstudianteArtefactoEntity;
import com.arquisoft.artefactos.domain.estudianteartefacto.AgregacionEstudiantesArtefactoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudiantesYaVinculadosArtefactoFinderImpl implements EstudiantesYaVinculadosArtefactoFinder {

    private final EstudianteArtefactoOutputPort estudianteArtefactoOutputPort;

    @Override
    public List<UUID> obtener(AgregacionEstudiantesArtefactoDomain entrada) {
        return estudianteArtefactoOutputPort.obtenerVinculados(entrada.getArtefacto(), entrada.getEstudiantes())
                .stream()
                .map(EstudianteArtefactoEntity::estudiante)
                .toList();
    }
}
