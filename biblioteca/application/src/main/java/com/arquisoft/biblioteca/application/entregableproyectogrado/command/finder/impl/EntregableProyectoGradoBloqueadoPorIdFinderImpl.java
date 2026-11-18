package com.arquisoft.biblioteca.application.entregableproyectogrado.command.finder.impl;

import com.arquisoft.biblioteca.application.entregableproyectogrado.command.finder.EntregableProyectoGradoBloqueadoPorIdFinder;
import com.arquisoft.biblioteca.application.entregableproyectogrado.command.secondaryport.EntregableProyectoGradoOutputPort;
import com.arquisoft.biblioteca.application.entregableproyectogrado.command.secondaryport.mapper.EntregableProyectoGradoMapper;
import com.arquisoft.biblioteca.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EntregableProyectoGradoBloqueadoPorIdFinderImpl implements EntregableProyectoGradoBloqueadoPorIdFinder {

    private final EntregableProyectoGradoOutputPort entregableProyectoGradoOutputPort;

    @Override
    public EntregableProyectoGradoDomain obtener(UUID id) {
        return entregableProyectoGradoOutputPort.obtenerBloqueadoPorId(id)
                .map(EntregableProyectoGradoMapper::toDomain)
                .orElse(EntregableProyectoGradoDomain.VACIO);
    }
}
