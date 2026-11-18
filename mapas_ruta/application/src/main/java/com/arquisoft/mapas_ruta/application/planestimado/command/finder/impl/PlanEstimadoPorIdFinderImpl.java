package com.arquisoft.mapas_ruta.application.planestimado.command.finder.impl;

import com.arquisoft.mapas_ruta.application.planestimado.command.finder.PlanEstimadoPorIdFinder;
import com.arquisoft.mapas_ruta.application.planestimado.command.secondaryport.PlanEstimadoOutputPort;
import com.arquisoft.mapas_ruta.application.planestimado.command.secondaryport.mapper.PlanEstimadoMapper;
import com.arquisoft.mapas_ruta.domain.planestimado.PlanEstimadoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PlanEstimadoPorIdFinderImpl implements PlanEstimadoPorIdFinder {

    private final PlanEstimadoOutputPort planEstimadoOutputPort;

    @Override
    public PlanEstimadoDomain obtener(UUID planEstimado) {
        return planEstimadoOutputPort.obtenerPorId(planEstimado)
                .map(PlanEstimadoMapper::toDomain)
                .orElse(PlanEstimadoDomain.VACIO);
    }
}
