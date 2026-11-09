package com.arquisoft.proyectos.application.entregableproyectogrado.command.finder.impl;

import com.arquisoft.proyectos.application.entregableproyectogrado.command.finder.EntregableProyectoGradoPorIdFinder;
import com.arquisoft.proyectos.application.entregableproyectogrado.command.secondaryport.EntregableProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.entregableproyectogrado.command.secondaryport.mapper.EntregableProyectoGradoMapper;
import com.arquisoft.proyectos.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EntregableProyectoGradoPorIdFinderImpl implements EntregableProyectoGradoPorIdFinder {

    private final EntregableProyectoGradoOutputPort entregableProyectoGradoOutputPort;

    @Override
    public EntregableProyectoGradoDomain obtener(UUID entregableProyectoGrado) {
        return entregableProyectoGradoOutputPort.obtenerPorId(entregableProyectoGrado)
                .map(EntregableProyectoGradoMapper::toDomain)
                .orElse(EntregableProyectoGradoDomain.VACIO);
    }
}
