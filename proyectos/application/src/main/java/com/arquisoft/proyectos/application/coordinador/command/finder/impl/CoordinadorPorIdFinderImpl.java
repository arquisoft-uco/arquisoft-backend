package com.arquisoft.proyectos.application.coordinador.command.finder.impl;

import com.arquisoft.proyectos.application.coordinador.command.finder.CoordinadorPorIdFinder;
import com.arquisoft.proyectos.application.coordinador.command.secondaryport.CoordinadorOutputPort;
import com.arquisoft.proyectos.application.coordinador.command.secondaryport.entity.CoordinadorEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CoordinadorPorIdFinderImpl implements CoordinadorPorIdFinder {

    private final CoordinadorOutputPort coordinadorOutputPort;

    @Override
    public Optional<CoordinadorEntity> obtener(UUID id) {
        return coordinadorOutputPort.obtenerPorId(id);
    }
}
