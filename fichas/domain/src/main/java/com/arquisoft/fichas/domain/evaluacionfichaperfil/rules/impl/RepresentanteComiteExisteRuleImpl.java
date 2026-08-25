package com.arquisoft.fichas.domain.evaluacionfichaperfil.rules.impl;

import com.arquisoft.fichas.domain.evaluacionfichaperfil.exception.RepresentanteComiteNoEncontradoException;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.ExistenciaRepresentanteComite;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.rules.RepresentanteComiteExisteRule;

public class RepresentanteComiteExisteRuleImpl implements RepresentanteComiteExisteRule {

    @Override
    public void validar(ExistenciaRepresentanteComite existencia) {
        if (!existencia.existe()) {
            throw new RepresentanteComiteNoEncontradoException(existencia.representanteComite());
        }
    }
}
