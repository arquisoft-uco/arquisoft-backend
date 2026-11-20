package com.arquisoft.biblioteca.application.entregableproyectogrado.command.finder.impl;

import com.arquisoft.biblioteca.application.entregableproyectogrado.command.finder.EntregableProyectoGradoExisteFinder;
import com.arquisoft.biblioteca.application.entregableproyectogrado.command.secondaryport.EntregableProyectoGradoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EntregableProyectoGradoExisteFinderImpl implements EntregableProyectoGradoExisteFinder {

    private final EntregableProyectoGradoOutputPort entregableProyectoGradoOutputPort;

    @Override
    public Boolean obtener(UUID id) {
        return entregableProyectoGradoOutputPort.existePorId(id);
    }
}
