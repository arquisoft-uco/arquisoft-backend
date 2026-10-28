package com.arquisoft.artefactos.application.estudianteartefacto.command.finder.impl;

import com.arquisoft.artefactos.application.estudianteartefacto.command.finder.EstudiantesPertenenciaProyectoGradoFinder;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.artefactos.domain.estudianteartefacto.AgregacionEstudiantesArtefactoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudiantesPertenenciaProyectoGradoFinderImpl implements EstudiantesPertenenciaProyectoGradoFinder {

    private final EstudianteProyectoGradoOutputPort estudianteProyectoGradoOutputPort;

    @Override
    public List<UUID> obtener(AgregacionEstudiantesArtefactoDomain entrada) {
        return estudianteProyectoGradoOutputPort
                .obtenerEstudiantesVigentesDelArtefacto(entrada.getArtefacto(), entrada.getEstudiantes());
    }
}
