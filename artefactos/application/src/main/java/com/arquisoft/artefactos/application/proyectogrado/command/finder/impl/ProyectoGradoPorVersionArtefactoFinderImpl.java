package com.arquisoft.artefactos.application.proyectogrado.command.finder.impl;

import com.arquisoft.artefactos.application.proyectogrado.command.finder.ProyectoGradoPorVersionArtefactoFinder;
import com.arquisoft.artefactos.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.artefactos.application.proyectogrado.command.secondaryport.mapper.ProyectoGradoMapper;
import com.arquisoft.artefactos.domain.proyectogrado.ProyectoGradoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProyectoGradoPorVersionArtefactoFinderImpl implements ProyectoGradoPorVersionArtefactoFinder {

    private final ProyectoGradoOutputPort proyectoGradoOutputPort;

    @Override
    public ProyectoGradoDomain obtener(UUID versionArtefacto) {
        return proyectoGradoOutputPort.obtenerPorVersionArtefacto(versionArtefacto)
                .map(ProyectoGradoMapper::toDomain)
                .orElse(ProyectoGradoDomain.VACIO);
    }
}
