package com.arquisoft.fichas.domain.evaluacionfichaperfil.rules.impl;

import com.arquisoft.fichas.domain.evaluacionfichaperfil.exception.EvaluacionFichaPerfilDuplicadaException;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.EvaluacionRepresentanteFichaCriteria;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.port.out.EvaluacionFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.rules.EvaluacionNoDuplicadaRule;

public class EvaluacionNoDuplicadaRuleImpl implements EvaluacionNoDuplicadaRule {

    private final EvaluacionFichaPerfilOutputPort evaluacionFichaPerfilOutputPort;

    public EvaluacionNoDuplicadaRuleImpl(EvaluacionFichaPerfilOutputPort evaluacionFichaPerfilOutputPort) {
        this.evaluacionFichaPerfilOutputPort = evaluacionFichaPerfilOutputPort;
    }

    @Override
    public void validar(EvaluacionRepresentanteFichaCriteria criteria) {
        if (evaluacionFichaPerfilOutputPort.existePorRepresentanteYFicha(
                criteria.representanteComite(), criteria.fichaPerfil())) {
            throw new EvaluacionFichaPerfilDuplicadaException(
                    criteria.representanteComite(), criteria.fichaPerfil());
        }
    }
}
