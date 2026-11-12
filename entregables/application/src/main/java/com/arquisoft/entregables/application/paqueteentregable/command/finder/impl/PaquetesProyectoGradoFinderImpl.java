package com.arquisoft.entregables.application.paqueteentregable.command.finder.impl;

import com.arquisoft.entregables.application.paqueteentregable.command.finder.PaquetesProyectoGradoFinder;
import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.PaqueteEntregableOutputPort;
import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.mapper.PaqueteEntregableMapper;
import com.arquisoft.entregables.domain.paqueteentregable.PaqueteEntregableDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaquetesProyectoGradoFinderImpl implements PaquetesProyectoGradoFinder {

    private final PaqueteEntregableOutputPort paqueteEntregableOutputPort;

    @Override
    public List<PaqueteEntregableDomain> obtener(UUID proyectoGrado) {
        return paqueteEntregableOutputPort.obtenerPorProyectoGrado(proyectoGrado).stream()
                .map(PaqueteEntregableMapper::toDomain)
                .toList();
    }
}
