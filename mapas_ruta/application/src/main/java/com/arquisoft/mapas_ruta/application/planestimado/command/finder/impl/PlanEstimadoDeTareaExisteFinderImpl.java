package com.arquisoft.mapas_ruta.application.planestimado.command.finder.impl;

import com.arquisoft.mapas_ruta.application.planestimado.command.finder.PlanEstimadoDeTareaExisteFinder;
import com.arquisoft.mapas_ruta.application.planestimado.command.secondaryport.PlanEstimadoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PlanEstimadoDeTareaExisteFinderImpl implements PlanEstimadoDeTareaExisteFinder {

    private final PlanEstimadoOutputPort planEstimadoOutputPort;

    @Override
    public Boolean obtener(UUID tarea) {
        return planEstimadoOutputPort.existePorTarea(tarea);
    }
}
