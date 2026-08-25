package com.arquisoft.fichas.domain.fichaperfil.rules.impl;

import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.domain.fichaperfil.model.ExistenciaFichaPerfil;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilExisteRule;

public class FichaPerfilExisteRuleImpl implements FichaPerfilExisteRule {

    @Override
    public void validar(ExistenciaFichaPerfil existencia) {
        if (!existencia.existe()) {
            throw new FichaPerfilNoEncontradaException(existencia.fichaPerfil());
        }
    }
}
