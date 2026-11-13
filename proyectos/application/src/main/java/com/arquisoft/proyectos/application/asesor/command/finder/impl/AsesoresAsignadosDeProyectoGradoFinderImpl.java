package com.arquisoft.proyectos.application.asesor.command.finder.impl;

import com.arquisoft.proyectos.application.asesor.command.finder.AsesoresAsignadosDeProyectoGradoFinder;
import com.arquisoft.proyectos.application.asesor.command.secondaryport.AsesorOutputPort;
import com.arquisoft.proyectos.application.asesor.command.secondaryport.mapper.AsesorMapper;
import com.arquisoft.proyectos.domain.asesor.AsesorDomain;
import com.arquisoft.proyectos.domain.estadoasesor.EstadoAsesor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesoresAsignadosDeProyectoGradoFinderImpl implements AsesoresAsignadosDeProyectoGradoFinder {

    private final AsesorOutputPort asesorOutputPort;

    @Override
    public List<AsesorDomain> obtener(UUID proyectoGrado) {
        return asesorOutputPort.obtenerAsignadosPorProyectoGrado(proyectoGrado, EstadoAsesor.ASIGNADO.getId())
                .stream()
                .map(AsesorMapper::toDomain)
                .toList();
    }
}
