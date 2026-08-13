package com.arquisoft.fichas.domain.estadofichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estadofichaperfil.exception.EstadoFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.domain.estadofichaperfil.model.ExistenciaEstadoFichaPerfil;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilExisteRule;

public class EstadoFichaPerfilExisteRuleImpl implements EstadoFichaPerfilExisteRule {

    @Override
    public void validar(ExistenciaEstadoFichaPerfil existencia) {
        if (!existencia.existe()) {
            throw new EstadoFichaPerfilNoEncontradoException(existencia.fichaPerfil());
        }
    }
}
