package com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.finder.impl;

import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.finder.VinculoEstudianteProyectoGradoFinder;
import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.secondaryport.mapper.EstudianteProyectoGradoMapper;
import com.arquisoft.evaluaciones.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VinculoEstudianteProyectoGradoFinderImpl implements VinculoEstudianteProyectoGradoFinder {

    private final EstudianteProyectoGradoOutputPort estudianteProyectoGradoOutputPort;

    @Override
    public EstudianteProyectoGradoDomain obtener(EstudianteProyectoGradoDomain vinculo) {
        return estudianteProyectoGradoOutputPort
                .obtenerPorEstudianteYProyectoGrado(vinculo.getEstudiante(), vinculo.getProyectoGrado())
                .map(EstudianteProyectoGradoMapper::toDomain)
                .orElse(EstudianteProyectoGradoDomain.VACIO);
    }
}
