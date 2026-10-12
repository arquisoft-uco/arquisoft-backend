package com.arquisoft.proyectos.application.estudianteproyectogrado.query.finder.impl;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.criteria.CompaneroProyectoGradoCriteria;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.finder.EstudianteVinculadoVigenteQueryFinder;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.secondaryport.EstudianteProyectoGradoAccesoQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EstudianteVinculadoVigenteQueryFinderImpl implements EstudianteVinculadoVigenteQueryFinder {

    private final EstudianteProyectoGradoAccesoQueryOutputPort estudianteProyectoGradoAccesoQueryOutputPort;

    @Override
    public Boolean obtener(CompaneroProyectoGradoCriteria criteria) {
        return estudianteProyectoGradoAccesoQueryOutputPort
                .existeVinculoVigente(criteria.proyectoGrado(), criteria.estudiante());
    }
}
