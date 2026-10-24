package com.arquisoft.artefactos.application.proyectogrado.command.finder.impl;

import com.arquisoft.artefactos.application.proyectogrado.command.finder.ProyectoGradoExisteFinder;
import com.arquisoft.artefactos.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProyectoGradoExisteFinderImpl implements ProyectoGradoExisteFinder {

    private final ProyectoGradoOutputPort proyectoGradoOutputPort;

    @Override
    public Boolean obtener(UUID id) {
        return proyectoGradoOutputPort.existePorId(id);
    }
}
