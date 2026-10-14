package com.arquisoft.proyectos.application.proyectogrado.query.finder.impl;

import com.arquisoft.proyectos.application.proyectogrado.query.finder.ProyectoGradoCoordinadorQueryFinder;
import com.arquisoft.proyectos.application.proyectogrado.query.secondaryport.ProyectoGradoAccesoQueryOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProyectoGradoCoordinadorQueryFinderImpl implements ProyectoGradoCoordinadorQueryFinder {

    private final ProyectoGradoAccesoQueryOutputPort proyectoGradoAccesoQueryOutputPort;

    @Override
    public UUID obtener(UUID proyectoGrado) {
        return proyectoGradoAccesoQueryOutputPort.obtenerCoordinadorPorId(proyectoGrado)
                .orElse(UtilUUID.obtenerUUIDPorDefecto());
    }
}
