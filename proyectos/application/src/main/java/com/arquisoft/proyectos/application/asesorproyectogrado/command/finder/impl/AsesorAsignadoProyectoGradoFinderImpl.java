package com.arquisoft.proyectos.application.asesorproyectogrado.command.finder.impl;

import com.arquisoft.proyectos.application.asesorproyectogrado.command.finder.AsesorAsignadoProyectoGradoFinder;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.secondaryport.AsesorProyectoGradoOutputPort;
import com.arquisoft.proyectos.domain.estadoasesor.EstadoAsesor;
import com.arquisoft.proyectos.domain.proyectogrado.CambioEstadoProyectoGradoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AsesorAsignadoProyectoGradoFinderImpl implements AsesorAsignadoProyectoGradoFinder {

    private final AsesorProyectoGradoOutputPort asesorProyectoGradoOutputPort;

    @Override
    public Boolean obtener(CambioEstadoProyectoGradoDomain entrada) {
        return asesorProyectoGradoOutputPort.existePorAsesorYProyectoGradoConEstadoVigente(
                entrada.getAsesor(), entrada.getProyectoGrado(), EstadoAsesor.ASIGNADO.getId());
    }
}
