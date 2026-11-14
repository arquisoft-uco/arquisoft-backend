package com.arquisoft.entregables.application.paqueteentregable.query.finder.impl;

import com.arquisoft.entregables.application.paqueteentregable.query.finder.PaqueteEntregableQueryFinder;
import com.arquisoft.entregables.application.paqueteentregable.query.secondaryport.PaqueteEntregableAccesoQueryOutputPort;
import com.arquisoft.entregables.application.paqueteentregable.query.secondaryport.mapper.PaqueteEntregableAccesoMapper;
import com.arquisoft.entregables.domain.paqueteentregable.PaqueteEntregableDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaqueteEntregableQueryFinderImpl implements PaqueteEntregableQueryFinder {

    private final PaqueteEntregableAccesoQueryOutputPort paqueteEntregableAccesoQueryOutputPort;

    @Override
    public PaqueteEntregableDomain obtener(UUID entregableProyectoGrado) {
        return paqueteEntregableAccesoQueryOutputPort.obtenerPorEntregable(entregableProyectoGrado)
                .map(PaqueteEntregableAccesoMapper::toDomain)
                .orElse(PaqueteEntregableDomain.VACIO);
    }
}
