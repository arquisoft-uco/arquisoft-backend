package com.arquisoft.proyectos.application.coordinador.command.finder.impl;

import com.arquisoft.proyectos.application.coordinador.command.finder.CoordinadorProyectosPorIdFinder;
import com.arquisoft.proyectos.application.coordinador.command.secondaryport.CoordinadorOutputPort;
import com.arquisoft.proyectos.application.coordinador.command.secondaryport.entity.CoordinadorEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CoordinadorProyectosPorIdFinderImpl implements CoordinadorProyectosPorIdFinder {

    private final CoordinadorOutputPort coordinadorOutputPort;

    @Override
    public Optional<CoordinadorEntity> obtener(UUID id) {
        return coordinadorOutputPort.obtenerPorId(id);
    }
}
