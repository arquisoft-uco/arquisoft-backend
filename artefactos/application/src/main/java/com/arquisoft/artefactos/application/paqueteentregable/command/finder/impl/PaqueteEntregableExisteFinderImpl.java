package com.arquisoft.artefactos.application.paqueteentregable.command.finder.impl;

import com.arquisoft.artefactos.application.paqueteentregable.command.finder.PaqueteEntregableExisteFinder;
import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.PaqueteEntregableOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaqueteEntregableExisteFinderImpl implements PaqueteEntregableExisteFinder {

    private final PaqueteEntregableOutputPort paqueteEntregableOutputPort;

    @Override
    public Boolean obtener(UUID entregableProyectoGrado) {
        return paqueteEntregableOutputPort.existePorEntregable(entregableProyectoGrado);
    }
}
