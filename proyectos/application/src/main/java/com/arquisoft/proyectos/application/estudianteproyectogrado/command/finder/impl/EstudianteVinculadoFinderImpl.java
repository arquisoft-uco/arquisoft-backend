package com.arquisoft.proyectos.application.estudianteproyectogrado.command.finder.impl;

import com.arquisoft.proyectos.application.estudianteproyectogrado.command.finder.EstudianteVinculadoFinder;
import com.arquisoft.proyectos.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.DestitucionEstudianteProyectoGradoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EstudianteVinculadoFinderImpl implements EstudianteVinculadoFinder {

    private final EstudianteProyectoGradoOutputPort estudianteProyectoGradoOutputPort;

    @Override
    public Boolean obtener(DestitucionEstudianteProyectoGradoDomain destitucion) {
        return estudianteProyectoGradoOutputPort.existeVinculo(
                destitucion.getProyectoGrado(), destitucion.getEstudiante());
    }
}
