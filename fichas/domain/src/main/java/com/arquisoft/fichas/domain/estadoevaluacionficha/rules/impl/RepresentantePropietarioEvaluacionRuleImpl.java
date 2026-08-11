package com.arquisoft.fichas.domain.estadoevaluacionficha.rules.impl;

import com.arquisoft.fichas.domain.estadoevaluacionficha.AgregacionEstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionFichaNoPropiaException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.RepresentantePropietarioEvaluacionRule;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.port.out.EvaluacionFichaPerfilOutputPort;

public class RepresentantePropietarioEvaluacionRuleImpl implements RepresentantePropietarioEvaluacionRule {

    private final EvaluacionFichaPerfilOutputPort evaluacionFichaPerfilOutputPort;

    public RepresentantePropietarioEvaluacionRuleImpl(
            EvaluacionFichaPerfilOutputPort evaluacionFichaPerfilOutputPort) {
        this.evaluacionFichaPerfilOutputPort = evaluacionFichaPerfilOutputPort;
    }

    @Override
    public void validar(AgregacionEstadoEvaluacionFichaDomain entrada) {
        if (!evaluacionFichaPerfilOutputPort.esRepresentantePropietario(
                entrada.getEvaluacionFichaPerfil(), entrada.getRepresentanteComite())) {
            throw new EvaluacionFichaNoPropiaException(entrada.getEvaluacionFichaPerfil());
        }
    }
}
