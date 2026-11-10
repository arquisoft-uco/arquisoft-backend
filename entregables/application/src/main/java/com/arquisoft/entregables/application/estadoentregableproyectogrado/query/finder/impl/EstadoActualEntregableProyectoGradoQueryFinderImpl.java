package com.arquisoft.entregables.application.estadoentregableproyectogrado.query.finder.impl;

import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.finder.EstadoActualEntregableProyectoGradoQueryFinder;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.secondaryport.EstadoEntregableProyectoGradoAccesoQueryOutputPort;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.secondaryport.mapper.EstadoEntregableProyectoGradoAccesoMapper;
import com.arquisoft.entregables.domain.estadoentregableproyectogrado.EstadoEntregableProyectoGradoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstadoActualEntregableProyectoGradoQueryFinderImpl
        implements EstadoActualEntregableProyectoGradoQueryFinder {

    private final EstadoEntregableProyectoGradoAccesoQueryOutputPort estadoAccesoQueryOutputPort;

    @Override
    public EstadoEntregableProyectoGradoDomain obtener(UUID entregableProyectoGrado) {
        return estadoAccesoQueryOutputPort.obtenerEstadoActual(entregableProyectoGrado)
                .map(EstadoEntregableProyectoGradoAccesoMapper::toDomain)
                .orElse(EstadoEntregableProyectoGradoDomain.VACIO);
    }
}
