package com.arquisoft.evaluaciones.application.evaluacion.command.validator;

import com.arquisoft.evaluaciones.domain.evaluacion.model.DestinatariosEvaluacionFinalizada;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ResumenesEvaluacion;

public interface FinalizarEvaluacionValidator {

    void validar(ResumenesEvaluacion resumenes, DestinatariosEvaluacionFinalizada destinatarios);
}
