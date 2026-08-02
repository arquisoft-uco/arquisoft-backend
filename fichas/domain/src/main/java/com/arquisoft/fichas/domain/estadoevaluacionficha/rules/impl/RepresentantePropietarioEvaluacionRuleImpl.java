package com.arquisoft.fichas.domain.estadoevaluacionficha.rules.impl;

import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionFichaNoPropiaException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.RepresentantePropietarioEvaluacionRule;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.PropietarioEvaluacionCriteria;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.port.out.EvaluacionFichaPerfilOutputPort;

public class RepresentantePropietarioEvaluacionRuleImpl implements RepresentantePropietarioEvaluacionRule {

    private final EvaluacionFichaPerfilOutputPort evaluacionFichaPerfilOutputPort;

    public RepresentantePropietarioEvaluacionRuleImpl(
            EvaluacionFichaPerfilOutputPort evaluacionFichaPerfilOutputPort) {
        this.evaluacionFichaPerfilOutputPort = evaluacionFichaPerfilOutputPort;
    }

    @Override
    public void validar(PropietarioEvaluacionCriteria criteria) {
        if (!evaluacionFichaPerfilOutputPort.esRepresentantePropietario(criteria)) {
            throw new EvaluacionFichaNoPropiaException(criteria.evaluacionFichaPerfil());
        }
    }
}
