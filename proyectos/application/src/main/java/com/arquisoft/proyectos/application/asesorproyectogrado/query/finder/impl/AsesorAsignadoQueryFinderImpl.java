package com.arquisoft.proyectos.application.asesorproyectogrado.query.finder.impl;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.finder.AsesorAsignadoQueryFinder;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.finder.model.AsignacionAsesorConsultada;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.secondaryport.AsesorProyectoGradoAccesoQueryOutputPort;
import com.arquisoft.proyectos.domain.estadoasesor.EstadoAsesor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AsesorAsignadoQueryFinderImpl implements AsesorAsignadoQueryFinder {

    private final AsesorProyectoGradoAccesoQueryOutputPort asesorProyectoGradoAccesoQueryOutputPort;

    @Override
    public Boolean obtener(AsignacionAsesorConsultada entrada) {
        return asesorProyectoGradoAccesoQueryOutputPort.existeAsignacionVigente(
                entrada.asesor(), entrada.proyectoGrado(), EstadoAsesor.ASIGNADO.getId());
    }
}
