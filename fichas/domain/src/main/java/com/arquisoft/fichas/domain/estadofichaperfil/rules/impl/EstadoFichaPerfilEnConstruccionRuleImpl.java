package com.arquisoft.fichas.domain.estadofichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estadofichaperfil.exception.FichaPerfilNoEnConstruccionException;
import com.arquisoft.fichas.domain.estadofichaperfil.model.EstadoActualFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilEnConstruccionRule;

public class EstadoFichaPerfilEnConstruccionRuleImpl implements EstadoFichaPerfilEnConstruccionRule {

    @Override
    public void validar(EstadoActualFicha estado) {
        if (!estado.estadoActual().permiteModificacion()) {
            throw new FichaPerfilNoEnConstruccionException(estado.estadoActual());
        }
    }
}
