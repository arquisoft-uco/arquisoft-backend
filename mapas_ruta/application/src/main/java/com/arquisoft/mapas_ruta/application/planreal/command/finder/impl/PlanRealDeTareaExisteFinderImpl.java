package com.arquisoft.mapas_ruta.application.planreal.command.finder.impl;

import com.arquisoft.mapas_ruta.application.planreal.command.finder.PlanRealDeTareaExisteFinder;
import com.arquisoft.mapas_ruta.application.planreal.command.secondaryport.PlanRealOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PlanRealDeTareaExisteFinderImpl implements PlanRealDeTareaExisteFinder {

    private final PlanRealOutputPort planRealOutputPort;

    @Override
    public Boolean obtener(UUID tarea) {
        return planRealOutputPort.existePorTarea(tarea);
    }
}
