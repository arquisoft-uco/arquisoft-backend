package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.exception.PuntajesCuantitativosAsesorExcedenValorItemException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model.PuntajesVsValoresMaximosAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.PuntajesCuantitativosAsesorNoExcedenValorItemRule;

import java.util.Map;
import java.util.stream.Collectors;

public class PuntajesCuantitativosAsesorNoExcedenValorItemRuleImpl
        implements PuntajesCuantitativosAsesorNoExcedenValorItemRule {

    @Override
    public void validar(PuntajesVsValoresMaximosAsesor entrada) {
        var excedidos = entrada.puntajes().entrySet().stream()
                .filter(par -> par.getValue() > entrada.valoresMaximos().get(par.getKey()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toUnmodifiableSet());

        if (!excedidos.isEmpty()) {
            throw new PuntajesCuantitativosAsesorExcedenValorItemException(excedidos);
        }
    }
}
