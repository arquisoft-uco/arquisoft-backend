package com.arquisoft.proyectos.application.asesorproyectogrado.command.finder.impl;

import com.arquisoft.proyectos.application.asesorproyectogrado.command.finder.AsesorYaAsignadoFinder;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.secondaryport.AsesorProyectoGradoOutputPort;
import com.arquisoft.proyectos.domain.asesorproyectogrado.AsignacionAsesorProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estadoasesor.EstadoAsesor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AsesorYaAsignadoFinderImpl implements AsesorYaAsignadoFinder {

    private final AsesorProyectoGradoOutputPort asesorProyectoGradoOutputPort;

    @Override
    public Boolean obtener(AsignacionAsesorProyectoGradoDomain entrada) {
        return asesorProyectoGradoOutputPort.existePorAsesorYProyectoGradoConEstadoVigente(
                entrada.getAsesor(), entrada.getProyectoGrado(), EstadoAsesor.ASIGNADO.getId());
    }
}
