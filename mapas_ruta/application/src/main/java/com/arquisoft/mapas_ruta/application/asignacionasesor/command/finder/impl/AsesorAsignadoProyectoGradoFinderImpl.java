package com.arquisoft.mapas_ruta.application.asignacionasesor.command.finder.impl;

import com.arquisoft.mapas_ruta.application.asignacionasesor.command.finder.AsesorAsignadoProyectoGradoFinder;
import com.arquisoft.mapas_ruta.application.asignacionasesor.command.finder.model.AsesorEnProyectoGrado;
import com.arquisoft.mapas_ruta.application.asignacionasesor.command.secondaryport.AsignacionAsesorOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AsesorAsignadoProyectoGradoFinderImpl implements AsesorAsignadoProyectoGradoFinder {

    private final AsignacionAsesorOutputPort asignacionAsesorOutputPort;

    @Override
    public Boolean obtener(AsesorEnProyectoGrado entrada) {
        return asignacionAsesorOutputPort.estaAsignado(entrada.proyectoGrado(), entrada.asesor());
    }
}
