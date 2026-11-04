package com.arquisoft.mapas_ruta.domain.planestimado.rules.impl;

import com.arquisoft.mapas_ruta.domain.planestimado.exception.TiempoPlanesExcedePlazoException;
import com.arquisoft.mapas_ruta.domain.planestimado.model.PlazoPlanesEstimados;
import com.arquisoft.mapas_ruta.domain.planestimado.rules.TiempoPlanesDentroDelPlazoRule;

import java.time.temporal.ChronoUnit;

public class TiempoPlanesDentroDelPlazoRuleImpl implements TiempoPlanesDentroDelPlazoRule {

    @Override
    public void validar(PlazoPlanesEstimados plazo) {
        var plazoDias = ChronoUnit.DAYS.between(plazo.fechaInicio(), plazo.fechaFin());
        var acumuladoConNuevo = plazo.diasAcumulados() + plazo.diasNuevoPlan();
        if (acumuladoConNuevo > plazoDias) {
            throw new TiempoPlanesExcedePlazoException(
                    plazo.mapaRuta(), plazo.diasAcumulados(), plazo.diasNuevoPlan(), plazoDias);
        }
    }
}
