package com.arquisoft.artefactos.application.estudianteartefacto.command.finder.impl;

import com.arquisoft.artefactos.application.estudianteartefacto.command.finder.EstudianteArtefactoExisteFinder;
import com.arquisoft.artefactos.application.estudianteartefacto.command.finder.model.ParticipacionEstudianteArtefacto;
import com.arquisoft.artefactos.application.estudianteartefacto.command.secondaryport.EstudianteArtefactoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EstudianteArtefactoExisteFinderImpl implements EstudianteArtefactoExisteFinder {

    private final EstudianteArtefactoOutputPort estudianteArtefactoOutputPort;

    @Override
    public Boolean obtener(ParticipacionEstudianteArtefacto participacion) {
        return estudianteArtefactoOutputPort.existePorArtefactoYEstudiante(
                participacion.artefacto(), participacion.estudiante());
    }
}
