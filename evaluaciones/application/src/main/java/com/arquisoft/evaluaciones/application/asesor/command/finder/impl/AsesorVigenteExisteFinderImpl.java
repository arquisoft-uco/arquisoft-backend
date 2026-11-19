package com.arquisoft.evaluaciones.application.asesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.asesor.command.finder.AsesorVigenteExisteFinder;
import com.arquisoft.evaluaciones.application.asesor.command.secondaryport.AsesorOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorVigenteExisteFinderImpl implements AsesorVigenteExisteFinder {

    private final AsesorOutputPort asesorOutputPort;

    @Override
    public Boolean obtener(UUID id) {
        return asesorOutputPort.existeVigentePorId(id);
    }
}
