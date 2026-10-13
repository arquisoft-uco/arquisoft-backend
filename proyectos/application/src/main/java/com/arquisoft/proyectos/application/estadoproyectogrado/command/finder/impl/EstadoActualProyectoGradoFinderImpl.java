package com.arquisoft.proyectos.application.estadoproyectogrado.command.finder.impl;

import com.arquisoft.proyectos.application.estadoproyectogrado.command.finder.EstadoActualProyectoGradoFinder;
import com.arquisoft.proyectos.application.estadoproyectogrado.command.secondaryport.EstadoProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.estadoproyectogrado.command.secondaryport.mapper.EstadoProyectoGradoMapper;
import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGradoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstadoActualProyectoGradoFinderImpl implements EstadoActualProyectoGradoFinder {

    private final EstadoProyectoGradoOutputPort estadoProyectoGradoOutputPort;

    @Override
    public EstadoProyectoGradoDomain obtener(UUID proyectoGrado) {
        return estadoProyectoGradoOutputPort.obtenerEstadoActual(proyectoGrado)
                .map(EstadoProyectoGradoMapper::toDomain)
                .orElse(EstadoProyectoGradoDomain.VACIO);
    }
}
