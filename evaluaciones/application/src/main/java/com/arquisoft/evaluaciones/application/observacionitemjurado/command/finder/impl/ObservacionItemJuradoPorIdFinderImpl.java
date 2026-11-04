package com.arquisoft.evaluaciones.application.observacionitemjurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.finder.ObservacionItemJuradoPorIdFinder;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.secondaryport.ObservacionItemJuradoOutputPort;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.secondaryport.mapper.ObservacionItemJuradoMapper;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.ObservacionItemJuradoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ObservacionItemJuradoPorIdFinderImpl implements ObservacionItemJuradoPorIdFinder {

    private final ObservacionItemJuradoOutputPort observacionItemJuradoOutputPort;

    @Override
    public ObservacionItemJuradoDomain obtener(UUID id) {
        return observacionItemJuradoOutputPort.obtenerPorId(id)
                .map(ObservacionItemJuradoMapper::toDomain)
                .orElse(ObservacionItemJuradoDomain.VACIO);
    }
}
