package com.arquisoft.proyectos.application.asesorproyectogrado.command.finder.impl;

import com.arquisoft.proyectos.application.asesorproyectogrado.command.finder.AsesoresAsignadosContadorFinder;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.secondaryport.AsesorProyectoGradoOutputPort;
import com.arquisoft.proyectos.domain.estadoasesor.EstadoAsesor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesoresAsignadosContadorFinderImpl implements AsesoresAsignadosContadorFinder {

    private final AsesorProyectoGradoOutputPort asesorProyectoGradoOutputPort;

    @Override
    public Long obtener(UUID proyectoGrado) {
        return asesorProyectoGradoOutputPort.contarPorProyectoGradoConEstadoVigente(
                proyectoGrado, EstadoAsesor.ASIGNADO.getId());
    }
}
