package com.arquisoft.biblioteca.application.estudianteproyectogrado.command.finder.impl;

import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.finder.VinculoEstudianteProyectoGradoFinder;
import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.secondaryport.mapper.EstudianteProyectoGradoMapper;
import com.arquisoft.biblioteca.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VinculoEstudianteProyectoGradoFinderImpl implements VinculoEstudianteProyectoGradoFinder {

    private final EstudianteProyectoGradoOutputPort estudianteProyectoGradoOutputPort;

    @Override
    public EstudianteProyectoGradoDomain obtener(EstudianteProyectoGradoDomain vinculo) {
        return estudianteProyectoGradoOutputPort
                .obtenerPorAutorYProyectoGrado(vinculo.getAutor(), vinculo.getProyectoGrado())
                .map(EstudianteProyectoGradoMapper::toDomain)
                .orElse(EstudianteProyectoGradoDomain.VACIO);
    }
}
