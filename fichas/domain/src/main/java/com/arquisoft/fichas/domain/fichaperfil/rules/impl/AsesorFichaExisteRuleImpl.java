package com.arquisoft.fichas.domain.fichaperfil.rules.impl;

import com.arquisoft.fichas.domain.fichaperfil.exception.AsesorFichaNoEncontradoException;
import com.arquisoft.fichas.domain.fichaperfil.model.ExistenciaAsesorFicha;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaExisteRule;

public class AsesorFichaExisteRuleImpl implements AsesorFichaExisteRule {

    @Override
    public void validar(ExistenciaAsesorFicha existencia) {
        if (!existencia.existe()) {
            throw new AsesorFichaNoEncontradoException(existencia.asesorFicha());
        }
    }
}
