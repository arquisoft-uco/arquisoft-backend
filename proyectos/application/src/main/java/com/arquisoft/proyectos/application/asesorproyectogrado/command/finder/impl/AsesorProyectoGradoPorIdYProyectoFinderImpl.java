package com.arquisoft.proyectos.application.asesorproyectogrado.command.finder.impl;

import com.arquisoft.proyectos.application.asesorproyectogrado.command.finder.AsesorProyectoGradoPorIdYProyectoFinder;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.secondaryport.AsesorProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.secondaryport.mapper.AsesorProyectoGradoMapper;
import com.arquisoft.proyectos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.proyectos.domain.asesorproyectogrado.CambioEstadoAsesorProyectoGradoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AsesorProyectoGradoPorIdYProyectoFinderImpl implements AsesorProyectoGradoPorIdYProyectoFinder {

    private final AsesorProyectoGradoOutputPort asesorProyectoGradoOutputPort;

    @Override
    public AsesorProyectoGradoDomain obtener(CambioEstadoAsesorProyectoGradoDomain entrada) {
        return asesorProyectoGradoOutputPort
                .obtenerPorIdYProyectoGrado(entrada.getAsesorProyectoGrado(), entrada.getProyectoGrado())
                .map(AsesorProyectoGradoMapper::toDomain)
                .orElse(AsesorProyectoGradoDomain.VACIO);
    }
}
