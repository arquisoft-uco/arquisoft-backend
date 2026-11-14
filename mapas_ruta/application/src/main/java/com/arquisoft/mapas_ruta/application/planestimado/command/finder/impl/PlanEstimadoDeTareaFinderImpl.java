package com.arquisoft.mapas_ruta.application.planestimado.command.finder.impl;

import com.arquisoft.mapas_ruta.application.planestimado.command.finder.PlanEstimadoDeTareaFinder;
import com.arquisoft.mapas_ruta.application.planestimado.command.secondaryport.PlanEstimadoOutputPort;
import com.arquisoft.mapas_ruta.application.planestimado.command.secondaryport.mapper.PlanEstimadoMapper;
import com.arquisoft.mapas_ruta.domain.planestimado.PlanEstimadoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PlanEstimadoDeTareaFinderImpl implements PlanEstimadoDeTareaFinder {

    private final PlanEstimadoOutputPort planEstimadoOutputPort;

    @Override
    public PlanEstimadoDomain obtener(UUID tarea) {
        return planEstimadoOutputPort.obtenerPorTarea(tarea)
                .map(PlanEstimadoMapper::toDomain)
                .orElse(PlanEstimadoDomain.VACIO);
    }
}
