package com.arquisoft.mapas_ruta.application.planestimado.command.finder.impl;

import com.arquisoft.mapas_ruta.application.planestimado.command.finder.DiasEstimadosAcumuladosDeMapaRutaFinder;
import com.arquisoft.mapas_ruta.application.planestimado.command.secondaryport.PlanEstimadoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DiasEstimadosAcumuladosDeMapaRutaFinderImpl implements DiasEstimadosAcumuladosDeMapaRutaFinder {

    private final PlanEstimadoOutputPort planEstimadoOutputPort;

    @Override
    public Long obtener(UUID tarea) {
        return planEstimadoOutputPort.sumarDiasEstimadosPorMapaRutaDeTarea(tarea);
    }
}
