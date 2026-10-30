package com.arquisoft.artefactos.application.estudianteartefacto.command.finder.impl;

import com.arquisoft.artefactos.application.estudianteartefacto.command.finder.EstudiantesVigentesVinculadosArtefactoFinder;
import com.arquisoft.artefactos.application.estudianteartefacto.command.secondaryport.EstudianteArtefactoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudiantesVigentesVinculadosArtefactoFinderImpl implements EstudiantesVigentesVinculadosArtefactoFinder {

    private final EstudianteArtefactoOutputPort estudianteArtefactoOutputPort;

    @Override
    public List<UUID> obtener(UUID artefacto) {
        return estudianteArtefactoOutputPort.obtenerVigentesVinculados(artefacto);
    }
}
