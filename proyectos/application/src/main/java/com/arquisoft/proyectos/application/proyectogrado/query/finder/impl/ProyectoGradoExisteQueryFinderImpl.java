package com.arquisoft.proyectos.application.proyectogrado.query.finder.impl;

import com.arquisoft.proyectos.application.proyectogrado.query.finder.ProyectoGradoExisteQueryFinder;
import com.arquisoft.proyectos.application.proyectogrado.query.secondaryport.ProyectoGradoAccesoQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProyectoGradoExisteQueryFinderImpl implements ProyectoGradoExisteQueryFinder {

    private final ProyectoGradoAccesoQueryOutputPort proyectoGradoAccesoQueryOutputPort;

    @Override
    public Boolean obtener(UUID proyectoGrado) {
        return proyectoGradoAccesoQueryOutputPort.existePorId(proyectoGrado);
    }
}
