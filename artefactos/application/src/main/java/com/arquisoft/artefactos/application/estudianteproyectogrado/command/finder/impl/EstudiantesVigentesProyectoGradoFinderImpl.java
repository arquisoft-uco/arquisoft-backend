package com.arquisoft.artefactos.application.estudianteproyectogrado.command.finder.impl;

import com.arquisoft.artefactos.application.estudianteproyectogrado.command.finder.EstudiantesVigentesProyectoGradoFinder;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudiantesVigentesProyectoGradoFinderImpl implements EstudiantesVigentesProyectoGradoFinder {

    private final EstudianteProyectoGradoOutputPort estudianteProyectoGradoOutputPort;

    @Override
    public List<UUID> obtener(UUID proyectoGrado) {
        return estudianteProyectoGradoOutputPort.obtenerVigentesPorProyectoGrado(proyectoGrado);
    }
}
