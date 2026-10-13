package com.arquisoft.proyectos.application.estudianteproyectogrado.command.finder.impl;

import com.arquisoft.proyectos.application.estudianteproyectogrado.command.finder.EstudiantesVinculadosContadorFinder;
import com.arquisoft.proyectos.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudiantesVinculadosContadorFinderImpl implements EstudiantesVinculadosContadorFinder {

    private final EstudianteProyectoGradoOutputPort estudianteProyectoGradoOutputPort;

    @Override
    public Long obtener(UUID proyectoGrado) {
        return estudianteProyectoGradoOutputPort.contarPorProyectoGrado(proyectoGrado);
    }
}
