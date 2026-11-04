package com.arquisoft.entregables.application.asesor.command.finder.impl;

import com.arquisoft.entregables.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.entregables.application.asesor.command.secondaryport.AsesorOutputPort;
import com.arquisoft.entregables.application.asesor.command.secondaryport.mapper.AsesorMapper;
import com.arquisoft.entregables.domain.asesor.AsesorDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorPorIdFinderImpl implements AsesorPorIdFinder {

    private final AsesorOutputPort asesorOutputPort;

    @Override
    public AsesorDomain obtener(UUID id) {
        return asesorOutputPort.obtenerPorId(id)
                .map(AsesorMapper::toDomain)
                .orElse(AsesorDomain.VACIO);
    }
}
