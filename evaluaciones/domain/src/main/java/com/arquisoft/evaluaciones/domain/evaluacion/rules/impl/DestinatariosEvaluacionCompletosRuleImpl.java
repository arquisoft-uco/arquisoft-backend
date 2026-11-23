package com.arquisoft.evaluaciones.domain.evaluacion.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacion.exception.DestinatariosEvaluacionIncompletosException;
import com.arquisoft.evaluaciones.domain.evaluacion.model.DestinatariosEvaluacionFinalizada;
import com.arquisoft.evaluaciones.domain.evaluacion.rules.DestinatariosEvaluacionCompletosRule;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionKey;
import com.arquisoft.shared.util.UtilTexto;

public class DestinatariosEvaluacionCompletosRuleImpl implements DestinatariosEvaluacionCompletosRule {

    @Override
    public void validar(DestinatariosEvaluacionFinalizada entrada) {
        if (entrada.coordinador().esVacio() || UtilTexto.esVacioONulo(entrada.coordinador().email())) {
            throw new DestinatariosEvaluacionIncompletosException(
                    entrada.evaluacion(), EvaluacionKey.ERROR_DESTINATARIOS_COORDINADOR);
        }
        if (entrada.asesor().esVacio() || UtilTexto.esVacioONulo(entrada.asesor().email())) {
            throw new DestinatariosEvaluacionIncompletosException(
                    entrada.evaluacion(), EvaluacionKey.ERROR_DESTINATARIOS_ASESOR);
        }
        if (entrada.estudiantesConCorreo().isEmpty()) {
            throw new DestinatariosEvaluacionIncompletosException(
                    entrada.evaluacion(), EvaluacionKey.ERROR_DESTINATARIOS_ESTUDIANTES);
        }
    }
}
