package com.arquisoft.entregables.application.asesor.command.finder.impl;

import com.arquisoft.entregables.application.asesor.command.finder.AsesorVigenteFinder;
import com.arquisoft.entregables.application.asesor.command.secondaryport.AsesorOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorVigenteFinderImpl implements AsesorVigenteFinder {

    private final AsesorOutputPort asesorOutputPort;

    @Override
    public Boolean obtener(UUID id) {
        return asesorOutputPort.existeVigentePorId(id);
    }
}
