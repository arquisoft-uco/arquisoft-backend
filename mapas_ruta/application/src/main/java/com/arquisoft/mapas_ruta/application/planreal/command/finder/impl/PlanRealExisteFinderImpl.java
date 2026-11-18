package com.arquisoft.mapas_ruta.application.planreal.command.finder.impl;

import com.arquisoft.mapas_ruta.application.planreal.command.finder.PlanRealExisteFinder;
import com.arquisoft.mapas_ruta.application.planreal.command.secondaryport.PlanRealOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PlanRealExisteFinderImpl implements PlanRealExisteFinder {

    private final PlanRealOutputPort planRealOutputPort;

    @Override
    public Boolean obtener(UUID planReal) {
        return planRealOutputPort.existePorId(planReal);
    }
}
