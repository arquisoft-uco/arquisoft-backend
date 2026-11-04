package com.arquisoft.entregables.application.entregableproyectogrado.command.finder.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.command.finder.EntregableProyectoGradoPorIdFinder;
import com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.EntregableProyectoGradoOutputPort;
import com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.mapper.EntregableProyectoGradoMapper;
import com.arquisoft.entregables.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EntregableProyectoGradoPorIdFinderImpl implements EntregableProyectoGradoPorIdFinder {

    private final EntregableProyectoGradoOutputPort entregableProyectoGradoOutputPort;

    @Override
    public EntregableProyectoGradoDomain obtener(UUID id) {
        return entregableProyectoGradoOutputPort.obtenerPorId(id)
                .map(EntregableProyectoGradoMapper::toDomain)
                .orElse(EntregableProyectoGradoDomain.VACIO);
    }
}
