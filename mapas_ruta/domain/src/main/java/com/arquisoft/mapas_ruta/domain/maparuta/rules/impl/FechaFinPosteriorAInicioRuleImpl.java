package com.arquisoft.mapas_ruta.domain.maparuta.rules.impl;

import com.arquisoft.mapas_ruta.domain.maparuta.exception.FechaFinMapaRutaNoPosteriorException;
import com.arquisoft.mapas_ruta.domain.maparuta.model.RangoFechasMapaRuta;
import com.arquisoft.mapas_ruta.domain.maparuta.rules.FechaFinPosteriorAInicioRule;

public class FechaFinPosteriorAInicioRuleImpl implements FechaFinPosteriorAInicioRule {

    @Override
    public void validar(RangoFechasMapaRuta rango) {
        if (!rango.fechaFin().isAfter(rango.fechaInicio())) {
            throw new FechaFinMapaRutaNoPosteriorException(rango.fechaFin(), rango.fechaInicio());
        }
    }
}
