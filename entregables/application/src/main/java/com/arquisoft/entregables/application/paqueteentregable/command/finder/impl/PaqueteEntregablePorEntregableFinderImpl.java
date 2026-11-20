package com.arquisoft.entregables.application.paqueteentregable.command.finder.impl;

import com.arquisoft.entregables.application.paqueteentregable.command.finder.PaqueteEntregablePorEntregableFinder;
import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.PaqueteEntregableOutputPort;
import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.mapper.PaqueteEntregableMapper;
import com.arquisoft.entregables.domain.paqueteentregable.PaqueteEntregableDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaqueteEntregablePorEntregableFinderImpl implements PaqueteEntregablePorEntregableFinder {

    private final PaqueteEntregableOutputPort paqueteEntregableOutputPort;

    @Override
    public PaqueteEntregableDomain obtener(UUID entregableProyectoGrado) {
        return paqueteEntregableOutputPort.obtenerPorEntregable(entregableProyectoGrado)
                .map(PaqueteEntregableMapper::toDomain)
                .orElse(PaqueteEntregableDomain.VACIO);
    }
}
