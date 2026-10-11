package com.arquisoft.fichas.application.representantecomite.command.finder.impl;

import com.arquisoft.fichas.application.representantecomite.command.finder.RepresentanteComitePorIdFinder;
import com.arquisoft.fichas.application.representantecomite.command.secondaryport.RepresentanteComiteOutputPort;
import com.arquisoft.fichas.application.representantecomite.command.secondaryport.mapper.RepresentanteComiteMapper;
import com.arquisoft.fichas.domain.representantecomite.RepresentanteComiteDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RepresentanteComitePorIdFinderImpl implements RepresentanteComitePorIdFinder {

    private final RepresentanteComiteOutputPort representanteComiteOutputPort;

    @Override
    public RepresentanteComiteDomain obtener(UUID id) {
        return representanteComiteOutputPort.obtenerPorId(id)
                .map(RepresentanteComiteMapper::toDomain)
                .orElse(RepresentanteComiteDomain.VACIO);
    }
}
