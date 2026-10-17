package com.arquisoft.mapas_ruta.application.asignacionproyecto.query.finder.impl;

import com.arquisoft.mapas_ruta.application.asignacionproyecto.query.finder.ProyectoGradoDeEstudianteQueryFinder;
import com.arquisoft.mapas_ruta.application.asignacionproyecto.query.secondaryport.ProyectoGradoDeEstudianteQueryOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProyectoGradoDeEstudianteQueryFinderImpl implements ProyectoGradoDeEstudianteQueryFinder {

    private final ProyectoGradoDeEstudianteQueryOutputPort proyectoGradoDeEstudianteQueryOutputPort;

    @Override
    public UUID obtener(UUID estudiante) {
        return proyectoGradoDeEstudianteQueryOutputPort.obtenerProyectoGrado(estudiante)
                .orElse(UtilUUID.obtenerUUIDPorDefecto());
    }
}
