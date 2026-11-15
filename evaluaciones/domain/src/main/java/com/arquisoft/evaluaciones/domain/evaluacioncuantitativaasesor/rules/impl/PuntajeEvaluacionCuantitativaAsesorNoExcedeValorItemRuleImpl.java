package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.exception.PuntajeEvaluacionCuantitativaAsesorExcedeValorItemException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model.PuntajeVsValorMaximoItemAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.PuntajeEvaluacionCuantitativaAsesorNoExcedeValorItemRule;

public class PuntajeEvaluacionCuantitativaAsesorNoExcedeValorItemRuleImpl
        implements PuntajeEvaluacionCuantitativaAsesorNoExcedeValorItemRule {

    @Override
    public void validar(PuntajeVsValorMaximoItemAsesor entrada) {
        if (entrada.puntaje() > entrada.valorMaximoItem()) {
            throw new PuntajeEvaluacionCuantitativaAsesorExcedeValorItemException(
                    entrada.puntaje(), entrada.valorMaximoItem(), entrada.nombreItem());
        }
    }
}
