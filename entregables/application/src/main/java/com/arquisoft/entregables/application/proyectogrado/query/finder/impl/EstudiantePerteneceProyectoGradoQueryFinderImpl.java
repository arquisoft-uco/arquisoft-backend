package com.arquisoft.entregables.application.proyectogrado.query.finder.impl;

import com.arquisoft.entregables.application.proyectogrado.query.finder.EstudiantePerteneceProyectoGradoQueryFinder;
import com.arquisoft.entregables.application.proyectogrado.query.finder.model.EstudianteProyectoGrado;
import com.arquisoft.entregables.application.proyectogrado.query.secondaryport.EstudianteProyectoGradoAccesoQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EstudiantePerteneceProyectoGradoQueryFinderImpl implements EstudiantePerteneceProyectoGradoQueryFinder {

    private final EstudianteProyectoGradoAccesoQueryOutputPort estudianteProyectoGradoAccesoQueryOutputPort;

    @Override
    public Boolean obtener(EstudianteProyectoGrado entrada) {
        return estudianteProyectoGradoAccesoQueryOutputPort.perteneceAlProyecto(
                entrada.estudiante(), entrada.proyectoGrado());
    }
}
