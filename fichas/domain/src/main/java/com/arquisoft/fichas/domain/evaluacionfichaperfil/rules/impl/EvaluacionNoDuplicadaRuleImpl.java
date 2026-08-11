package com.arquisoft.fichas.domain.evaluacionfichaperfil.rules.impl;

import com.arquisoft.fichas.domain.evaluacionfichaperfil.EvaluacionFichaPerfilDomain;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.exception.EvaluacionFichaPerfilDuplicadaException;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.secondaryport.EvaluacionFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.rules.EvaluacionNoDuplicadaRule;

public class EvaluacionNoDuplicadaRuleImpl implements EvaluacionNoDuplicadaRule {

    private final EvaluacionFichaPerfilOutputPort evaluacionFichaPerfilOutputPort;

    public EvaluacionNoDuplicadaRuleImpl(EvaluacionFichaPerfilOutputPort evaluacionFichaPerfilOutputPort) {
        this.evaluacionFichaPerfilOutputPort = evaluacionFichaPerfilOutputPort;
    }

    @Override
    public void validar(EvaluacionFichaPerfilDomain evaluacion) {
        if (evaluacionFichaPerfilOutputPort.existePorRepresentanteYFicha(
                evaluacion.getRepresentanteComiteId(), evaluacion.getFichaPerfilId())) {
            throw new EvaluacionFichaPerfilDuplicadaException(
                    evaluacion.getRepresentanteComiteId(), evaluacion.getFichaPerfilId());
        }
    }
}
