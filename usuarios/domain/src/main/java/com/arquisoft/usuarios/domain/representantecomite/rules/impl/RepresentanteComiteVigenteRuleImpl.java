package com.arquisoft.usuarios.domain.representantecomite.rules.impl;

import com.arquisoft.usuarios.domain.representantecomite.exception.RepresentanteComiteNoEncontradoException;
import com.arquisoft.usuarios.domain.representantecomite.model.ExistenciaRepresentanteComite;
import com.arquisoft.usuarios.domain.representantecomite.rules.RepresentanteComiteVigenteRule;

public class RepresentanteComiteVigenteRuleImpl implements RepresentanteComiteVigenteRule {

    @Override
    public void validar(ExistenciaRepresentanteComite existencia) {
        var representanteComite = existencia.representanteComite();
        if (representanteComite.esVacio() || representanteComite.estaEliminado()) {
            throw new RepresentanteComiteNoEncontradoException(existencia.usuario());
        }
    }
}
