package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.PuntajesCuantitativosJuradoExcedenValorItemException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.PuntajesVsValoresMaximosJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.PuntajesCuantitativosJuradoNoExcedenValorItemRule;

import java.util.Map;
import java.util.stream.Collectors;

public class PuntajesCuantitativosJuradoNoExcedenValorItemRuleImpl
        implements PuntajesCuantitativosJuradoNoExcedenValorItemRule {

    @Override
    public void validar(PuntajesVsValoresMaximosJurado entrada) {
        var excedidos = entrada.puntajes().entrySet().stream()
                .filter(par -> par.getValue() > entrada.valoresMaximos().get(par.getKey()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toUnmodifiableSet());

        if (!excedidos.isEmpty()) {
            throw new PuntajesCuantitativosJuradoExcedenValorItemException(excedidos);
        }
    }
}
