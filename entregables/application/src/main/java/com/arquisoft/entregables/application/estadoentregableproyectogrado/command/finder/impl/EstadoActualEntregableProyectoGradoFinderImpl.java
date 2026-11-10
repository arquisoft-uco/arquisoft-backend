package com.arquisoft.entregables.application.estadoentregableproyectogrado.command.finder.impl;

import com.arquisoft.entregables.application.estadoentregableproyectogrado.command.finder.EstadoActualEntregableProyectoGradoFinder;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.command.secondaryport.EstadoEntregableProyectoGradoOutputPort;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.command.secondaryport.mapper.EstadoEntregableProyectoGradoMapper;
import com.arquisoft.entregables.domain.estadoentregableproyectogrado.EstadoEntregableProyectoGradoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstadoActualEntregableProyectoGradoFinderImpl implements EstadoActualEntregableProyectoGradoFinder {

    private final EstadoEntregableProyectoGradoOutputPort estadoEntregableProyectoGradoOutputPort;

    @Override
    public EstadoEntregableProyectoGradoDomain obtener(UUID entregableProyectoGrado) {
        return estadoEntregableProyectoGradoOutputPort.obtenerEstadoActual(entregableProyectoGrado)
                .map(EstadoEntregableProyectoGradoMapper::toDomain)
                .orElse(EstadoEntregableProyectoGradoDomain.VACIO);
    }
}
