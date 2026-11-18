package com.arquisoft.proyectos.application.entregableproyectogrado.command.finder.impl;

import com.arquisoft.proyectos.application.entregableproyectogrado.command.finder.UltimoEntregableProyectoGradoFinder;
import com.arquisoft.proyectos.application.entregableproyectogrado.command.secondaryport.EntregableProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.entregableproyectogrado.command.secondaryport.mapper.EntregableProyectoGradoMapper;
import com.arquisoft.proyectos.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UltimoEntregableProyectoGradoFinderImpl implements UltimoEntregableProyectoGradoFinder {

    private final EntregableProyectoGradoOutputPort entregableProyectoGradoOutputPort;

    @Override
    public EntregableProyectoGradoDomain obtener(UUID proyectoGrado) {
        return entregableProyectoGradoOutputPort.obtenerUltimoPorProyectoGrado(proyectoGrado)
                .map(EntregableProyectoGradoMapper::toDomain)
                .orElse(EntregableProyectoGradoDomain.VACIO);
    }
}
