package com.arquisoft.artefactos.application.estudianteartefacto.command.finder.impl;

import com.arquisoft.artefactos.application.estudianteartefacto.command.finder.EstudiantesPertenenciaProyectoGradoFinder;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.artefactos.domain.estudianteartefacto.AgregacionEstudiantesArtefactoDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.ParEstudianteArtefacto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EstudiantesPertenenciaProyectoGradoFinderImpl implements EstudiantesPertenenciaProyectoGradoFinder {

    private final EstudianteProyectoGradoOutputPort estudianteProyectoGradoOutputPort;

    @Override
    public List<ParEstudianteArtefacto> obtener(AgregacionEstudiantesArtefactoDomain entrada) {
        return estudianteProyectoGradoOutputPort
                .obtenerPertenenciasVigentes(entrada.getArtefactos(), entrada.getEstudiantes())
                .stream()
                .map(pertenencia -> new ParEstudianteArtefacto(pertenencia.artefacto(), pertenencia.estudiante()))
                .toList();
    }
}
