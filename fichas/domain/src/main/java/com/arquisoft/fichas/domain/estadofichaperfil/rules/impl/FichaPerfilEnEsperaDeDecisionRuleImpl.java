package com.arquisoft.fichas.domain.estadofichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.FichaPerfilEnEsperaDeDecisionException;
import com.arquisoft.fichas.domain.estadofichaperfil.model.FichaEnEsperaDeDecision;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.FichaPerfilEnEsperaDeDecisionRule;

public class FichaPerfilEnEsperaDeDecisionRuleImpl implements FichaPerfilEnEsperaDeDecisionRule {

    @Override
    public void validar(FichaEnEsperaDeDecision ficha) {
        if (ficha.estadoActual() == EstadoFicha.DISPONIBLE_PARA_EVALUACION && ficha.finalizadas() > 0) {
            throw new FichaPerfilEnEsperaDeDecisionException(
                    ficha.fichaPerfil(), ficha.tituloProyecto(), ficha.finalizadas());
        }
    }
}
