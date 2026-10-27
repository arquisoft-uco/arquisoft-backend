package com.arquisoft.artefactos.application.estudianteartefacto.command.finder.impl;

import com.arquisoft.artefactos.application.estudianteartefacto.command.finder.EstudiantesYaVinculadosArtefactoFinder;
import com.arquisoft.artefactos.application.estudianteartefacto.command.secondaryport.EstudianteArtefactoOutputPort;
import com.arquisoft.artefactos.application.estudianteartefacto.command.secondaryport.mapper.EstudianteArtefactoMapper;
import com.arquisoft.artefactos.domain.estudianteartefacto.AgregacionEstudiantesArtefactoDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.ParEstudianteArtefacto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EstudiantesYaVinculadosArtefactoFinderImpl implements EstudiantesYaVinculadosArtefactoFinder {

    private final EstudianteArtefactoOutputPort estudianteArtefactoOutputPort;

    @Override
    public List<ParEstudianteArtefacto> obtener(AgregacionEstudiantesArtefactoDomain entrada) {
        return estudianteArtefactoOutputPort.obtenerVinculados(entrada.getArtefactos(), entrada.getEstudiantes())
                .stream()
                .map(EstudianteArtefactoMapper::toPar)
                .toList();
    }
}
