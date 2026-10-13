package com.arquisoft.proyectos.application.estadoproyectogrado.query.finder.impl;

import com.arquisoft.proyectos.application.estadoproyectogrado.query.criteria.EstadoProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.finder.EstudianteVinculadoEstadoProyectoGradoQueryFinder;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.secondaryport.EstudianteProyectoGradoAccesoQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EstudianteVinculadoEstadoProyectoGradoQueryFinderImpl
        implements EstudianteVinculadoEstadoProyectoGradoQueryFinder {

    private final EstudianteProyectoGradoAccesoQueryOutputPort estudianteProyectoGradoAccesoQueryOutputPort;

    @Override
    public Boolean obtener(EstadoProyectoGradoEstudianteCriteria criteria) {
        return estudianteProyectoGradoAccesoQueryOutputPort
                .existeVinculoVigente(criteria.proyectoGrado(), criteria.estudiante());
    }
}
