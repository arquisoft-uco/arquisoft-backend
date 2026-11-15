package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.validator;

import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.CambioPuntajeEvaluacionCuantitativaAsesorDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.ContextoEvaluacionCuantitativaAsesorDomain;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.ItemCuantitativoAsesorDomain;

public interface CambiarPuntajeEvaluacionCuantitativaAsesorValidator {

    void validar(
            CambioPuntajeEvaluacionCuantitativaAsesorDomain cambio,
            ContextoEvaluacionCuantitativaAsesorDomain contexto,
            ItemCuantitativoAsesorDomain item);
}
