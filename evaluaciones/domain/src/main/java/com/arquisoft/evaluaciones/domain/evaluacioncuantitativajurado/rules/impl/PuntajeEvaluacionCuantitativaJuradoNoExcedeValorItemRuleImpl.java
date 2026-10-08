package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.PuntajeEvaluacionCuantitativaJuradoExcedeValorItemException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.PuntajeVsValorMaximoItem;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.PuntajeEvaluacionCuantitativaJuradoNoExcedeValorItemRule;

public class PuntajeEvaluacionCuantitativaJuradoNoExcedeValorItemRuleImpl
        implements PuntajeEvaluacionCuantitativaJuradoNoExcedeValorItemRule {

    @Override
    public void validar(PuntajeVsValorMaximoItem puntajeVsValorMaximoItem) {
        if (puntajeVsValorMaximoItem.puntaje() > puntajeVsValorMaximoItem.valorMaximoItem()) {
            throw new PuntajeEvaluacionCuantitativaJuradoExcedeValorItemException(
                    puntajeVsValorMaximoItem.puntaje(), puntajeVsValorMaximoItem.valorMaximoItem());
        }
    }
}
