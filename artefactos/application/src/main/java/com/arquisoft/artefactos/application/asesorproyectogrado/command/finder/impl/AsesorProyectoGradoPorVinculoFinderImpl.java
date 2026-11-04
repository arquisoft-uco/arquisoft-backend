package com.arquisoft.artefactos.application.asesorproyectogrado.command.finder.impl;

import com.arquisoft.artefactos.application.asesorproyectogrado.command.finder.AsesorProyectoGradoPorVinculoFinder;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.finder.model.VinculoAsesorProyectoGrado;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.secondaryport.AsesorProyectoGradoOutputPort;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.secondaryport.mapper.AsesorProyectoGradoMapper;
import com.arquisoft.artefactos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AsesorProyectoGradoPorVinculoFinderImpl implements AsesorProyectoGradoPorVinculoFinder {

    private final AsesorProyectoGradoOutputPort asesorProyectoGradoOutputPort;

    @Override
    public AsesorProyectoGradoDomain obtener(VinculoAsesorProyectoGrado vinculo) {
        return asesorProyectoGradoOutputPort
                .obtenerPorAsesorYProyectoGrado(vinculo.asesor(), vinculo.proyectoGrado())
                .map(AsesorProyectoGradoMapper::toDomain)
                .orElse(AsesorProyectoGradoDomain.VACIO);
    }
}
