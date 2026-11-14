package com.arquisoft.entregables.application.proyectogrado.command.finder.impl;

import com.arquisoft.entregables.application.proyectogrado.command.finder.ProyectoGradoBloqueadoDeEntregableFinder;
import com.arquisoft.entregables.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.entregables.application.proyectogrado.command.secondaryport.mapper.ProyectoGradoMapper;
import com.arquisoft.entregables.domain.proyectogrado.ProyectoGradoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProyectoGradoBloqueadoDeEntregableFinderImpl implements ProyectoGradoBloqueadoDeEntregableFinder {

    private final ProyectoGradoOutputPort proyectoGradoOutputPort;

    @Override
    public ProyectoGradoDomain obtener(UUID entregable) {
        return proyectoGradoOutputPort.obtenerBloqueadoPorEntregable(entregable)
                .map(ProyectoGradoMapper::toDomain)
                .orElse(ProyectoGradoDomain.VACIO);
    }
}
