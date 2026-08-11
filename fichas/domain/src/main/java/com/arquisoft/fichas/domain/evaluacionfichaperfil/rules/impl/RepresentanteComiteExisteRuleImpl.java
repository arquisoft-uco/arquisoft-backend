package com.arquisoft.fichas.domain.evaluacionfichaperfil.rules.impl;

import com.arquisoft.fichas.domain.evaluacionfichaperfil.exception.RepresentanteComiteNoEncontradoException;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.rules.RepresentanteComiteExisteRule;
import com.arquisoft.fichas.domain.representantecomite.secondaryport.RepresentanteComiteOutputPort;

import java.util.UUID;

public class RepresentanteComiteExisteRuleImpl implements RepresentanteComiteExisteRule {

    private final RepresentanteComiteOutputPort representanteComiteOutputPort;

    public RepresentanteComiteExisteRuleImpl(RepresentanteComiteOutputPort representanteComiteOutputPort) {
        this.representanteComiteOutputPort = representanteComiteOutputPort;
    }

    @Override
    public void validar(UUID representanteComite) {
        if (!representanteComiteOutputPort.existePorId(representanteComite)) {
            throw new RepresentanteComiteNoEncontradoException(representanteComite);
        }
    }
}
