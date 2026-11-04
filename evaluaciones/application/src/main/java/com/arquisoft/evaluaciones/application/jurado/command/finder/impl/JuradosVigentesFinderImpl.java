package com.arquisoft.evaluaciones.application.jurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.jurado.command.finder.JuradosVigentesFinder;
import com.arquisoft.evaluaciones.application.jurado.command.secondaryport.JuradoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JuradosVigentesFinderImpl implements JuradosVigentesFinder {

    private final JuradoOutputPort juradoOutputPort;

    @Override
    public Set<UUID> obtener(Set<UUID> jurados) {
        return juradoOutputPort.consultarVigentesPorIds(jurados);
    }
}
