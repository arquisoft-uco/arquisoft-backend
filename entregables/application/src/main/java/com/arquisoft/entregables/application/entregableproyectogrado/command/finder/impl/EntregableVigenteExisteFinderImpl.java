package com.arquisoft.entregables.application.entregableproyectogrado.command.finder.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.command.finder.EntregableVigenteExisteFinder;
import com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.EntregableProyectoGradoOutputPort;
import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EntregableVigenteExisteFinderImpl implements EntregableVigenteExisteFinder {

    private final EntregableProyectoGradoOutputPort entregableProyectoGradoOutputPort;

    @Override
    public Boolean obtener(UUID proyectoGrado) {
        return entregableProyectoGradoOutputPort.existeVigentePorProyectoGrado(
                proyectoGrado, EstadoEntregable.CANCELADO.getId());
    }
}
