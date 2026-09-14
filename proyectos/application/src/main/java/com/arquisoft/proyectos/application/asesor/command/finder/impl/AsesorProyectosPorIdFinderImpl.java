package com.arquisoft.proyectos.application.asesor.command.finder.impl;

import com.arquisoft.proyectos.application.asesor.command.finder.AsesorProyectosPorIdFinder;
import com.arquisoft.proyectos.application.asesor.command.secondaryport.AsesorOutputPort;
import com.arquisoft.proyectos.application.asesor.command.secondaryport.entity.AsesorEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorProyectosPorIdFinderImpl implements AsesorProyectosPorIdFinder {

    private final AsesorOutputPort asesorOutputPort;

    @Override
    public Optional<AsesorEntity> obtener(UUID id) {
        return asesorOutputPort.obtenerPorId(id);
    }
}
