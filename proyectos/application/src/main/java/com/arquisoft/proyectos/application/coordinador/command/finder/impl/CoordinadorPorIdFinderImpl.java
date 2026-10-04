package com.arquisoft.proyectos.application.coordinador.command.finder.impl;

import com.arquisoft.proyectos.application.coordinador.command.finder.CoordinadorPorIdFinder;
import com.arquisoft.proyectos.application.coordinador.command.secondaryport.CoordinadorOutputPort;
import com.arquisoft.proyectos.application.coordinador.command.secondaryport.mapper.CoordinadorMapper;
import com.arquisoft.proyectos.domain.coordinador.CoordinadorDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CoordinadorPorIdFinderImpl implements CoordinadorPorIdFinder {

    private final CoordinadorOutputPort coordinadorOutputPort;

    @Override
    public CoordinadorDomain obtener(UUID id) {
        return coordinadorOutputPort.obtenerPorId(id)
                .map(CoordinadorMapper::toDomain)
                .orElse(CoordinadorDomain.VACIO);
    }
}
