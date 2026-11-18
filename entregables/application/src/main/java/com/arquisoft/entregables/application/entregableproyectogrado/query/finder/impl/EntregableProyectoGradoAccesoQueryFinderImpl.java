package com.arquisoft.entregables.application.entregableproyectogrado.query.finder.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.query.finder.EntregableProyectoGradoAccesoQueryFinder;
import com.arquisoft.entregables.application.entregableproyectogrado.query.secondaryport.EntregableProyectoGradoAccesoQueryOutputPort;
import com.arquisoft.entregables.application.entregableproyectogrado.query.secondaryport.mapper.EntregableProyectoGradoAccesoMapper;
import com.arquisoft.entregables.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EntregableProyectoGradoAccesoQueryFinderImpl implements EntregableProyectoGradoAccesoQueryFinder {

    private final EntregableProyectoGradoAccesoQueryOutputPort entregableProyectoGradoAccesoQueryOutputPort;

    @Override
    public EntregableProyectoGradoDomain obtener(UUID entregableProyectoGrado) {
        return entregableProyectoGradoAccesoQueryOutputPort.obtenerAccesoPorId(entregableProyectoGrado)
                .map(EntregableProyectoGradoAccesoMapper::toDomain)
                .orElse(EntregableProyectoGradoDomain.VACIO);
    }
}
