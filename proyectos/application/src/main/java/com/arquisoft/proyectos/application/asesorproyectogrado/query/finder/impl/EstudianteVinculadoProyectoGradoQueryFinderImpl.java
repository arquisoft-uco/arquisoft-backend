package com.arquisoft.proyectos.application.asesorproyectogrado.query.finder.impl;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.criteria.AsesorProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.finder.EstudianteVinculadoProyectoGradoQueryFinder;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.secondaryport.EstudianteProyectoGradoAccesoQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EstudianteVinculadoProyectoGradoQueryFinderImpl implements EstudianteVinculadoProyectoGradoQueryFinder {

    private final EstudianteProyectoGradoAccesoQueryOutputPort estudianteProyectoGradoAccesoQueryOutputPort;

    @Override
    public Boolean obtener(AsesorProyectoGradoEstudianteCriteria criteria) {
        return estudianteProyectoGradoAccesoQueryOutputPort
                .existeVinculoVigente(criteria.proyectoGrado(), criteria.estudiante());
    }
}
