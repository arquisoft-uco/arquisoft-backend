package com.arquisoft.artefactos.application.observacion.command.finder.impl;

import com.arquisoft.artefactos.application.observacion.command.finder.ObservacionPorIdFinder;
import com.arquisoft.artefactos.application.observacion.command.secondaryport.ObservacionOutputPort;
import com.arquisoft.artefactos.application.observacion.command.secondaryport.mapper.ObservacionMapper;
import com.arquisoft.artefactos.domain.observacion.ObservacionDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ObservacionPorIdFinderImpl implements ObservacionPorIdFinder {

    private final ObservacionOutputPort observacionOutputPort;

    @Override
    public ObservacionDomain obtener(UUID id) {
        return observacionOutputPort.obtenerPorId(id)
                .map(ObservacionMapper::toDomain)
                .orElse(ObservacionDomain.VACIO);
    }
}
