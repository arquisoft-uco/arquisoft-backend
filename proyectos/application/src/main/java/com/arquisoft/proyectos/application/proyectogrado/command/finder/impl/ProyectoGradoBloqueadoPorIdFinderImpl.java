package com.arquisoft.proyectos.application.proyectogrado.command.finder.impl;

import com.arquisoft.proyectos.application.proyectogrado.command.finder.ProyectoGradoBloqueadoPorIdFinder;
import com.arquisoft.proyectos.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.proyectogrado.command.secondaryport.mapper.ProyectoGradoMapper;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProyectoGradoBloqueadoPorIdFinderImpl implements ProyectoGradoBloqueadoPorIdFinder {

    private final ProyectoGradoOutputPort proyectoGradoOutputPort;

    @Override
    public ProyectoGradoDomain obtener(UUID proyectoGrado) {
        return proyectoGradoOutputPort.obtenerBloqueadoPorId(proyectoGrado)
                .map(ProyectoGradoMapper::toDomain)
                .orElse(ProyectoGradoDomain.VACIO);
    }
}
