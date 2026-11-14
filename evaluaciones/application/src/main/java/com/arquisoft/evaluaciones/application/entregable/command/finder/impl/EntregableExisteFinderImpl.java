package com.arquisoft.evaluaciones.application.entregable.command.finder.impl;

import com.arquisoft.evaluaciones.application.entregable.command.finder.EntregableExisteFinder;
import com.arquisoft.evaluaciones.application.entregable.command.secondaryport.EntregableOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EntregableExisteFinderImpl implements EntregableExisteFinder {

    private final EntregableOutputPort entregableOutputPort;

    @Override
    public Boolean obtener(UUID id) {
        return entregableOutputPort.existePorId(id);
    }
}
