package com.arquisoft.artefactos.application.estudianteproyectogrado.command.finder.impl;

import com.arquisoft.artefactos.application.estudianteproyectogrado.command.finder.EstudianteProyectoGradoPorVinculoFinder;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.finder.model.VinculoEstudianteProyectoGrado;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport.mapper.EstudianteProyectoGradoMapper;
import com.arquisoft.artefactos.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EstudianteProyectoGradoPorVinculoFinderImpl implements EstudianteProyectoGradoPorVinculoFinder {

    private final EstudianteProyectoGradoOutputPort estudianteProyectoGradoOutputPort;

    @Override
    public EstudianteProyectoGradoDomain obtener(VinculoEstudianteProyectoGrado vinculo) {
        return estudianteProyectoGradoOutputPort
                .obtenerPorEstudianteYProyectoGrado(vinculo.estudiante(), vinculo.proyectoGrado())
                .map(EstudianteProyectoGradoMapper::toDomain)
                .orElse(EstudianteProyectoGradoDomain.VACIO);
    }
}
