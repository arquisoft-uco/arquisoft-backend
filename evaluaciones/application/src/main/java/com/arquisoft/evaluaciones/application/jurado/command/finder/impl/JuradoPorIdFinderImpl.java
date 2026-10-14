package com.arquisoft.evaluaciones.application.jurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.jurado.command.finder.JuradoPorIdFinder;
import com.arquisoft.evaluaciones.application.jurado.command.secondaryport.JuradoOutputPort;
import com.arquisoft.evaluaciones.application.jurado.command.secondaryport.mapper.JuradoMapper;
import com.arquisoft.evaluaciones.domain.jurado.JuradoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JuradoPorIdFinderImpl implements JuradoPorIdFinder {

    private final JuradoOutputPort juradoOutputPort;

    @Override
    public JuradoDomain obtener(UUID id) {
        return juradoOutputPort.obtenerPorId(id)
                .map(JuradoMapper::toDomain)
                .orElse(JuradoDomain.VACIO);
    }
}
