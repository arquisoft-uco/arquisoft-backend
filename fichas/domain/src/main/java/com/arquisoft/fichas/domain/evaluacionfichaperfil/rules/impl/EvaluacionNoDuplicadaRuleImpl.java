package com.arquisoft.fichas.domain.evaluacionfichaperfil.rules.impl;

import com.arquisoft.fichas.domain.evaluacionfichaperfil.exception.EvaluacionFichaPerfilDuplicadaException;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.DisponibilidadEvaluacionFicha;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.rules.EvaluacionNoDuplicadaRule;

public class EvaluacionNoDuplicadaRuleImpl implements EvaluacionNoDuplicadaRule {

    @Override
    public void validar(DisponibilidadEvaluacionFicha disponibilidad) {
        if (disponibilidad.yaExiste()) {
            throw new EvaluacionFichaPerfilDuplicadaException(
                    disponibilidad.representanteComite(), disponibilidad.fichaPerfil());
        }
    }
}
